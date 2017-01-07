package com.mercateo.common.rest.schemagen.link;

import com.google.common.collect.Iterables;
import com.googlecode.gentyref.GenericTypeReflector;
import com.mercateo.common.rest.schemagen.JsonSchemaGenerator;
import com.mercateo.common.rest.schemagen.link.relation.Relation;

import javax.ws.rs.BeanParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Link.Builder;
import javax.ws.rs.core.UriBuilder;
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


    private final LinkFactoryContext linkFactoryContext;

    LinkCreator(LinkFactoryContext linkFactoryContext) {
        this.linkFactoryContext = requireNonNull(linkFactoryContext);
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
     * @param scopes   list of Scope objects for every scope level
     * @param relation relation of method
     * @return link with schema if applicable
     */
    public Link createFor(List<Scope> scopes, Relation relation) {
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

        final URI uri = uriBuilder.buildFromMap(pathParameters);

        Builder builder = setRelation(relation, uri);

        addLinkProperties(scopes, builder);

        detectMediaType(scopes, builder);

        final Scope lastScopedMethod = Iterables.getLast(scopes);
        addHttpMethod(builder, lastScopedMethod);
        addSchemaIfNeeded(builder, lastScopedMethod);
        if (linkFactoryContext.getBaseUri() != null) {
            builder.baseUri(linkFactoryContext.getBaseUri());
        }
        return builder.build();
    }

    private void addLinkProperties(List<Scope> scopes, Builder builder) {
        final LinkProperties properties = Iterables.getLast(scopes).getInvokedMethod()
                .getAnnotation(LinkProperties.class);
        if (properties != null) {
            Stream.of(properties.value()).forEach(x -> builder.param(x.key(), x.value()));
        }
    }

    private void detectMediaType(Collection<Scope> scopes, Builder builder) {
        final Method method = Iterables.getLast(scopes).getInvokedMethod();
        Optional.ofNullable(method.getAnnotation(Produces.class)).ifPresent(produces -> {
            final String[] values = produces.value();
            if (values.length > 0) {
                builder.param("mediaType", values[0]);
            }
        });
    }

    private Map<String, Object> collectPathParameters(Scope scope,
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
        }, scope.getInvokedMethod(), parameters);

        return pathParameters;
    }

    private void setQueryParameters(final UriBuilder uriBuilder, Scope scope,
                                    Object[] parameters) {
        Type[] realParamTypes = GenericTypeReflector.getExactParameterTypes(scope
                .getInvokedMethod(), scope.getInvokedClass());
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

    private void addSchemaIfNeeded(Builder builder, Scope method) {
        final JsonSchemaGenerator schemaGenerator = linkFactoryContext.getSchemaGenerator();
        Optional<String> optionalInputSchema = schemaGenerator.createInputSchema(method,
                linkFactoryContext.getFieldCheckerForSchema());
        optionalInputSchema.ifPresent(s -> builder.param(SCHEMA_PARAM_KEY, s));

        Optional<String> optionalOutputSchema = schemaGenerator.createOutputSchema(method,
                linkFactoryContext.getFieldCheckerForSchema());
        optionalOutputSchema.ifPresent(s -> builder.param(TARGET_SCHEMA_PARAM_KEY, s));
    }

}
