package com.mercateo.common.rest.schemagen;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.BeanParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.reflections.ReflectionUtils;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mercateo.common.rest.schemagen.annotation.Media;
import com.mercateo.common.rest.schemagen.generator.JsonPropertyResult;
import com.mercateo.common.rest.schemagen.generator.ObjectContext;
import com.mercateo.common.rest.schemagen.generator.ObjectContextBuilder;
import com.mercateo.common.rest.schemagen.generictype.GenericType;
import com.mercateo.common.rest.schemagen.json.mapper.PropertyJsonSchemaMapper;
import com.mercateo.common.rest.schemagen.link.Scope;
import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;

public class RestJsonSchemaGenerator implements JsonSchemaGenerator {
    private static final Set<Class<?>> INVALID_OUTPUT_TYPES = new HashSet<>(Arrays.asList(void.class,
            Void.class));

    public static final HashSet<Class<? extends Annotation>> PAYLOAD_ANNOTATIONS = new HashSet<>(Arrays.asList(
            QueryParam.class, FormParam.class, FormDataParam.class));

    private final SchemaPropertyGenerator schemaPropertyGenerator;

    private final PropertyJsonSchemaMapper propertyJsonSchemaMapper;

    public RestJsonSchemaGenerator() {
        schemaPropertyGenerator = new SchemaPropertyGenerator();
        propertyJsonSchemaMapper = new PropertyJsonSchemaMapper();
    }

    @Override
    public Optional<String> createOutputSchema(Scope scope,
            FieldCheckerForSchema fieldCheckerForSchema) {

        final GenericType<?> genericType = GenericType.of(scope.getReturnType(), scope
                .getInvokedMethod().getReturnType());

        if (!INVALID_OUTPUT_TYPES.contains(genericType.getRawType())) {
            return generateJsonSchema(ObjectContext.buildFor(genericType).build(),
                    createSchemaPropertyContext(scope, fieldCheckerForSchema)).map(
                            Object::toString);
        } else {
            return Optional.empty();
        }
    }

    private SchemaPropertyContext createSchemaPropertyContext(Scope method,
            FieldCheckerForSchema fieldCheckerForSchema) {
        return new SchemaPropertyContext(method.getCallContext().get(), fieldCheckerForSchema);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<String> createInputSchema(Scope scope,
            FieldCheckerForSchema fieldCheckerForSchema) {
        Map<String, ObjectNode> objectNodes = new HashMap<>();

        final Type[] types = scope.getParameterTypes();

        for (int i = 0; i < types.length; i++) {
            final Type parameterType = types[i];
            Annotation[] parameterAnnotations = scope.getInvokedMethod().getParameterAnnotations()[i];

            Optional<Media> media = Optional.empty();
            boolean ignore = false;

            Optional<String> name = Optional.empty();
            for (Annotation paramAn : parameterAnnotations) {
                if (paramAn instanceof QueryParam) {
                    ignore = true;
                } else if (paramAn instanceof PathParam) {
                    ignore = true;
                } else if (paramAn instanceof BeanParam) {
                    ignore = doesNotContainPayload(parameterType);
                } else if (paramAn instanceof HeaderParam) {
                    ignore = true;
                } else if (paramAn instanceof FormDataParam) {
                    ignore = true;
                } else if (paramAn instanceof Context) {
                    ignore = true;
                } else if (paramAn instanceof FormParam) {
                    FormParam formParam = (FormParam) paramAn;
                    name = Optional.of(formParam.value());
                } else if (paramAn instanceof Media) {
                    media = Optional.of((Media) paramAn);
                }
            }

            if (!ignore) {
                @SuppressWarnings("rawtypes")
                final ObjectContextBuilder objectContextBuilder = ObjectContext.buildFor(
                        GenericType.of(parameterType));

                if (scope.hasAllowedValues(i)) {
                    final List<Object> allowedValues = scope.getAllowedValues(i);
                    objectContextBuilder.withAllowedValues(allowedValues);
                }
                if (scope.getParams() != null && scope.getParams().length > i) {
                    objectContextBuilder.withCurrentValue(scope.getParams()[i]);
                }

                if (scope.hasDefaultValue(i)) {
                    objectContextBuilder.withDefaultValue(scope.getDefaultValue(i));
                }

                final Optional<ObjectNode> objectNodeOption = generateJsonSchema(
                        objectContextBuilder.build(), createSchemaPropertyContext(scope,
                                fieldCheckerForSchema));
                if (!objectNodeOption.isPresent()) {
                    continue;
                }
                ObjectNode objectNode = objectNodeOption.get();

                if (media.isPresent()) {
                    objectNode.put("mediaType", media.get().type());
                    objectNode.put("binaryEncoding", media.get().binaryEncoding());
                }

                final String propertyName = name.orElse("");
                if (objectNodes.containsKey(propertyName)) {
                    throw new IllegalStateException("multiple properties named <" + propertyName
                            + "> found");
                }
                objectNodes.put(propertyName, objectNode);
            }
        }

        if (objectNodes.isEmpty()) {
            return Optional.empty();
        } else if (objectNodes.size() == 1) {
            final Collection<ObjectNode> values = objectNodes.values();
            return Optional.of(values.iterator().next().toString());
        } else {
            final ObjectNode objectNode = createObjectNode();
            for (Map.Entry<String, ObjectNode> entry : objectNodes.entrySet()) {
                objectNode.set(entry.getKey(), entry.getValue());
            }
            return Optional.of(objectNode.toString());
        }
    }

    private boolean doesNotContainPayload(Type type) {
        //noinspection unchecked
        return ReflectionUtils
            .getAllFields((Class<?>) type, field -> PAYLOAD_ANNOTATIONS.stream().anyMatch(
                    annotationClass -> field.isAnnotationPresent(annotationClass)))
            .isEmpty();
    }

    private Optional<ObjectNode> generateJsonSchema(ObjectContext<?> objectContext,
            SchemaPropertyContext schemaPropertyContext) {
        final JsonPropertyResult jsonPropertyResult = schemaPropertyGenerator.generateSchemaProperty(objectContext,
                schemaPropertyContext);
        JsonProperty rootJsonProperty = jsonPropertyResult.getRoot();
        if (rootJsonProperty.getProperties().isEmpty() && rootJsonProperty.getType() == PropertyType.OBJECT) {
            return Optional.empty();
        }
        return Optional.of(propertyJsonSchemaMapper.toJson(jsonPropertyResult));
    }

    private ObjectNode createObjectNode() {
        return new ObjectNode(new JsonNodeFactory(true));
    }
}
