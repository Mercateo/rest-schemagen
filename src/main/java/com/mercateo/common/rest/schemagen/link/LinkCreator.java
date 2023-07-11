/**
 * Copyright © 2015 Mercateo AG (http://www.mercateo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mercateo.common.rest.schemagen.link;

import com.google.common.collect.Iterables;
import com.googlecode.gentyref.GenericTypeReflector;
import com.mercateo.common.rest.schemagen.JsonSchemaGenerator;
import com.mercateo.common.rest.schemagen.link.relation.Relation;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.Link.Builder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.mercateo.common.rest.schemagen.link.helper.ParameterAnnotationVisitor.visitAnnotations;
import static java.util.Objects.requireNonNull;

public class LinkCreator {
    public static final String TARGET_SCHEMA_PARAM_KEY = "targetSchema";

    public static final String SCHEMA_PARAM_KEY = "schema";

    public static final String METHOD_PARAM_KEY = "method";

    private final JsonSchemaGenerator jsonSchemaGenerator;

    private final LinkFactoryContext linkFactoryContext;

    /**
     * @param jsonSchemaGenerator
     * @param linkFactoryContext
     *
     */
    LinkCreator(JsonSchemaGenerator jsonSchemaGenerator, LinkFactoryContext linkFactoryContext) {
        this.jsonSchemaGenerator = requireNonNull(jsonSchemaGenerator);
        this.linkFactoryContext = linkFactoryContext;
    }

    public static Builder setRelation(Relation relation, URI uri) {
        requireNonNull(relation);
        requireNonNull(uri);
        Builder builder = Link.fromUri(uri).rel(relation.getName());
        if (requireNonNull(relation).getType().isShouldBeSerialized()) {
            builder.param("relType", relation.getType().getName());
            builder.param("target", relation.getType().getSerializedName());
        }
        return builder;
    }

    /**
     * create a link for a resource method
     *
     * @param scopes
     *            list of Scope objects for every scope level
     * @param relation
     *            relation of method
     * @return link with schema if applicable
     */
    public Link createFor(List<Scope> scopes, Relation relation) {
        return createFor(scopes, relation, requireNonNull(linkFactoryContext));
    }

    /**
     * create a link for a resource method
     *
     * @param scopes
     *            list of Scope objects for every scope level
     * @param relation
     *            relation of method
     * @param linkFactoryContext
     *            the base URI for resolution of relative URIs and method and
     *            property checkers
     * @return link with schema if applicable
     */
    public Link createFor(List<Scope> scopes, Relation relation,
            LinkFactoryContext linkFactoryContext) {
        final Class<?> resourceClass = scopes.get(0).getInvokedClass();
        UriBuilder uriBuilder = UriBuilder.fromResource(resourceClass);

        Map<String, Object> pathParameters = new HashMap<>();
        for (Scope scope : scopes) {
            final Method method = scope.getInvokedMethod();
            final Object[] parameters = scope.getParams();
            if (method.isAnnotationPresent(Path.class)) {
                uriBuilder.path(method.getDeclaringClass(), method.getName());
            }
            pathParameters.putAll(collectPathParameters(scope, parameters));
            setQueryParameters(uriBuilder, scope, parameters);
        }

        URI uri = mergeUri(linkFactoryContext.getBaseUri(), uriBuilder, pathParameters);

        Builder builder = setRelation(relation, uri);

        addLinkProperties(scopes, builder);

        detectMediaType(scopes, builder);

        final Scope lastScopedMethod = Iterables.getLast(scopes);
        addHttpMethod(builder, lastScopedMethod);
        addSchemaIfNeeded(builder, lastScopedMethod, linkFactoryContext);
        return builder.build();
    }

    private URI mergeUri(URI baseUri, UriBuilder uriBuilder, Map<String, Object> pathParameters) {
        URI uri = uriBuilder.buildFromMap(pathParameters);

        if (baseUri != null) {
            UriBuilder mergedUriBuilder = UriBuilder.fromUri(baseUri);
            mergedUriBuilder.path(uri.getPath());
            mergedUriBuilder.replaceQuery(uri.getQuery());
            return mergedUriBuilder.buildFromMap(pathParameters);
        }

        return uri;
    }

    private void addLinkProperties(List<Scope> scopes, Builder builder) {
        final LinkProperties properties = Iterables.getLast(scopes).getInvokedMethod()
                .getAnnotation(LinkProperties.class);
        if (properties != null) {
            Stream.of(properties.value()).forEach(x -> builder.param(x.key(), x.value()));
        }
    }

    private void detectMediaType(Collection<Scope> scopes, Builder builder) {
        detectMediaType(Iterables.getLast(scopes).getInvokedMethod()).ifPresent(mediatype -> builder
                .param("mediaType", mediatype));
    }

    private Optional<String> detectMediaType(Method method) {
        Produces annotation = method.getAnnotation(Produces.class);
        if (annotation == null) {
            annotation = method.getDeclaringClass().getAnnotation(Produces.class);
        }

        return Optional.ofNullable(annotation).map(produces -> {
            final String[] values = produces.value();
            if (values.length > 0) {
                return values[0];
            }
            return null;
        });
    }

    private Map<String, Object> collectPathParameters(Scope scope, Object[] parameters) {
        final Map<String, Object> pathParameters = new HashMap<>();
        visitAnnotations((parameter, parameterIndex, annotation) -> {
            if (annotation instanceof PathParam) {
                PathParam pathParamAnnotation = (PathParam) annotation;
                pathParameters.put(pathParamAnnotation.value(), parameter);
            } else if (annotation instanceof BeanParam) {
                BeanParamExtractor beanParamExtractor = new BeanParamExtractor();
                pathParameters.putAll(beanParamExtractor.getPathParameters(parameter));
            }
        }, scope.getInvokedMethod(), parameters);

        return pathParameters;
    }

    private void setQueryParameters(final UriBuilder uriBuilder, Scope scope, Object[] parameters) {
        Type[] realParamTypes = GenericTypeReflector.getExactParameterTypes(scope
                .getInvokedMethod(), scope.getInvokedClass());
        visitAnnotations((parameter, parameterIndex, annotation) -> {
            if (annotation instanceof QueryParam && parameter != null) {
                final String parameterName = ((QueryParam) annotation).value();
                if (parameter instanceof Iterable) {
                    uriBuilder.queryParam(parameterName, Iterables.toArray((Iterable) parameter, Object.class));
                } else {
                    uriBuilder.queryParam(parameterName, parameter.toString());
                }
            } else if (annotation instanceof BeanParam && parameter != null) {
                if (realParamTypes[parameterIndex] instanceof Class<?>) {
                    BeanParamExtractor beanParamExtractor = new BeanParamExtractor();
                    Map<String, Object[]> queryParameter = beanParamExtractor.getQueryParameters(
                            parameter);
                    queryParameter.forEach((uriBuilder::queryParam));
                }
            }
        }, scope.getInvokedMethod(), parameters);
    }

    private void addHttpMethod(Builder builder, Scope scope) {
        final List<Class<? extends Annotation>> httpMethodAnnotations = Arrays.asList(GET.class,
                POST.class, PUT.class, DELETE.class);
        final Method invokedMethod = scope.getInvokedMethod();
        final Optional<Class<? extends Annotation>> httpMethod = httpMethodAnnotations.stream()
                .filter(invokedMethod::isAnnotationPresent).findFirst();

        if (httpMethod.isPresent()) {
            builder.param(METHOD_PARAM_KEY, httpMethod.get().getSimpleName());
        } else {
            throw new IllegalArgumentException(
                    "LinkCreator: The method has to be annotated with one of: " + String.join(", ",
                            (Iterable<String>) httpMethodAnnotations.stream().map(
                                    Class::getSimpleName).map(m -> '@' + m)::iterator));
        }
    }

    private void addSchemaIfNeeded(Builder builder, Scope method,
            LinkFactoryContext linkFactoryContext) {
        Optional<String> optionalInputSchema = jsonSchemaGenerator.createInputSchema(method,
                linkFactoryContext.getFieldCheckerForSchema());
        optionalInputSchema.ifPresent(s -> builder.param(SCHEMA_PARAM_KEY, s));
        Optional<String> mt = detectMediaType(method.getInvokedMethod());
        if (mt.isPresent() && MediaType.APPLICATION_JSON.equals(mt.get())) {
            Optional<String> optionalOutputSchema = jsonSchemaGenerator.createOutputSchema(method,
                    linkFactoryContext.getFieldCheckerForSchema(),
                    linkFactoryContext.getTargetSchemaEnablerForLink());
            optionalOutputSchema.ifPresent(s -> builder.param(TARGET_SCHEMA_PARAM_KEY, s));
        }
    }

}
