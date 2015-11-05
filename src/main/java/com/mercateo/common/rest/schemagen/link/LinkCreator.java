package com.mercateo.common.rest.schemagen.link;

import static com.mercateo.common.rest.schemagen.link.helper.ParameterAnnotationVisitor.visitAnnotations;
import static java.util.Objects.requireNonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.ws.rs.BeanParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Link.Builder;
import javax.ws.rs.core.UriBuilder;

import com.google.common.collect.Iterables;
import com.googlecode.gentyref.GenericTypeReflector;
import com.mercateo.common.rest.schemagen.JsonSchemaGenerator;
import com.mercateo.common.rest.schemagen.link.relation.Relation;

public class LinkCreator {
    public static final String TARGET_SCHEMA_PARAM_KEY = "targetSchema";

    public static final String SCHEMA_PARAM_KEY = "schema";

    public static final String METHOD_PARAM_KEY = "method";

    private final LinkFactoryContext linkFactoryContext;

    LinkCreator(LinkFactoryContext linkFactoryContext) {
        this.linkFactoryContext = requireNonNull(linkFactoryContext);
    }

    /**
     * create a link for a resource method
     *
     * @param scopeMethods
     *            list of ScopeMethod objects for every scope level
     *
     * @param relation
     *            relation of method
     *
     * @return link with schema if applicable
     */
    public Link createFor(List<ScopeMethod> scopeMethods, Relation relation) {
        final Class<?> resourceClass = scopeMethods.get(0).getInvokedClass();
        UriBuilder uriBuilder = UriBuilder.fromResource(resourceClass);

        Map<String, Object> pathParameters = new HashMap<>();
        for (ScopeMethod scopeMethod : scopeMethods) {
            final Method method = scopeMethod.getInvokedMethod();
            final Object[] parameters = scopeMethod.getParams();
            if (method.isAnnotationPresent(Path.class)) {
                uriBuilder.path(method.getDeclaringClass(), method.getName());
            }
            pathParameters.putAll(collectPathParameters(scopeMethod, parameters));
            setQueryParameters(uriBuilder, scopeMethod, parameters);
        }

        final URI uri = uriBuilder.buildFromMap(pathParameters);
        Builder builder = Link.fromUri(uri).rel(relation.getName());

        addLinkProperties(scopeMethods, builder);

        if (requireNonNull(relation).getType().isShouldBeSerialized()) {
            builder.param("relType", relation.getType().getName());
            builder.param("target", relation.getType().getSerializedName());
        }

        final ScopeMethod lastScopedMethod = Iterables.getLast(scopeMethods);
        addHttpMethod(builder, lastScopedMethod);
        addSchemaIfNeeded(builder, lastScopedMethod);
        if (linkFactoryContext.getBaseUri() != null) {
            builder.baseUri(linkFactoryContext.getBaseUri());
        }
        return builder.build();
    }

    private void addLinkProperties(List<ScopeMethod> scopeMethods, Builder builder) {
        final LinkProperties properties = Iterables.getLast(scopeMethods).getInvokedMethod()
                .getAnnotation(LinkProperties.class);
        if (properties != null) {
            Stream.of(properties.value()).forEach(x -> builder.param(x.key(), x.value()));
        }
    }

    private Map<String, Object> collectPathParameters(ScopeMethod scopeMethod,
            Object[] parameters) {
        final Map<String, Object> pathParameters = new HashMap<>();
        visitAnnotations((parameter, parameterIndex, annotation) -> {
            if (annotation instanceof PathParam) {
                PathParam pathParamAnnotation = (PathParam) annotation;
                pathParameters.put(pathParamAnnotation.value(), parameter);
            } else if (annotation instanceof BeanParam) {
                BeanParamExtractor beanParamExtractor = new BeanParamExtractor();
                pathParameters.putAll(beanParamExtractor.getPathParameters(parameter));
            }
        } , scopeMethod.getInvokedMethod(), parameters);

        return pathParameters;
    }

    private void setQueryParameters(final UriBuilder uriBuilder, ScopeMethod scopeMethod,
            Object[] parameters) {
        Type[] realParamTypes = GenericTypeReflector.getExactParameterTypes(scopeMethod
                .getInvokedMethod(), scopeMethod.getInvokedClass());
        visitAnnotations((parameter, parameterIndex, annotation) -> {
            if (annotation instanceof QueryParam && parameter != null) {
                uriBuilder.queryParam(((QueryParam) annotation).value(), parameter.toString());
            } else if (annotation instanceof BeanParam && parameter != null) {
                if (realParamTypes[parameterIndex] instanceof Class<?>) {
                    BeanParamExtractor beanParamExtractor = new BeanParamExtractor();
                    Map<String, Object[]> queryParameter = beanParamExtractor.getQueryParameters(
                            parameter);
                    queryParameter.forEach((uriBuilder::queryParam));
                }
            }
        } , scopeMethod.getInvokedMethod(), parameters);
    }

    private void addHttpMethod(Builder builder, ScopeMethod scopeMethod) {
        final List<Class<? extends Annotation>> httpMethodAnnotations = Arrays.asList(GET.class,
                POST.class, PUT.class, DELETE.class);
        final Method invokedMethod = scopeMethod.getInvokedMethod();
        final Optional<Class<? extends Annotation>> httpMethod = httpMethodAnnotations.stream()
                .filter(invokedMethod::isAnnotationPresent).findFirst();

        if (httpMethod.isPresent()) {
            builder.param(METHOD_PARAM_KEY, httpMethod.get().getSimpleName());
        } else {
            throw new IllegalArgumentException(
                    "LinkCreator: The method has to be annotated with one of: " + String.join(", ",
                            (Iterable<String>) httpMethodAnnotations.stream().map(
                                    Class::getSimpleName)::iterator));
        }
    }

    private void addSchemaIfNeeded(Builder builder, ScopeMethod method) {
        final JsonSchemaGenerator schemaGenerator = linkFactoryContext.getSchemaGenerator();
        Optional<String> optionalInputSchema = schemaGenerator.createInputSchema(method,
                linkFactoryContext.getFieldCheckerForSchema());
        if (optionalInputSchema.isPresent()) {
            builder.param(SCHEMA_PARAM_KEY, optionalInputSchema.get());
        }

        Optional<String> optionalOutputSchema = schemaGenerator.createOutputSchema(method,
                linkFactoryContext.getFieldCheckerForSchema());
        if (optionalOutputSchema.isPresent()) {
            builder.param(TARGET_SCHEMA_PARAM_KEY, optionalOutputSchema.get());
        }
    }

}
