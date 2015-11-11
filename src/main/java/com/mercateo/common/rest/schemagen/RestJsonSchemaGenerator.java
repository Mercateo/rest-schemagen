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

import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mercateo.common.rest.schemagen.annotation.Media;
import com.mercateo.common.rest.schemagen.generator.ObjectContext;
import com.mercateo.common.rest.schemagen.generictype.GenericType;
import com.mercateo.common.rest.schemagen.link.ScopeMethod;
import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;

public class RestJsonSchemaGenerator implements JsonSchemaGenerator {
    static final Set<Class<?>> INVALID_OUTPUT_TYPES = new HashSet<>(Arrays.asList(void.class,
            Void.class));

    private final static Logger logger = LoggerFactory.getLogger(RestJsonSchemaGenerator.class);

    private final SchemaPropertyGenerator schemaPropertyGenerator;

    private final PropertyJsonSchemaMapper propertyJsonSchemaMapper;

    public RestJsonSchemaGenerator() {
        schemaPropertyGenerator = new SchemaPropertyGenerator();
        propertyJsonSchemaMapper = new PropertyJsonSchemaMapper();
    }

    @Override
    public Optional<String> createOutputSchema(ScopeMethod method,
            FieldCheckerForSchema fieldCheckerForSchema) {
        logger.debug("createOutputSchema {}", method);
        final GenericType<?> genericType = GenericType.of(method.getReturnType(), method
                .getInvokedMethod().getReturnType());

        if (!INVALID_OUTPUT_TYPES.contains(genericType.getRawType())) {
            return generateJsonSchema(ObjectContext.buildFor(genericType).build(),
                    createSchemaPropertyContext(method, fieldCheckerForSchema)).map(
                            Object::toString);
        } else {
            return Optional.empty();
        }
    }

    private SchemaPropertyContext createSchemaPropertyContext(ScopeMethod method,
            FieldCheckerForSchema fieldCheckerForSchema) {
        return new SchemaPropertyContext(method.getCallContext(), fieldCheckerForSchema);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<String> createInputSchema(ScopeMethod method,
            FieldCheckerForSchema fieldCheckerForSchema) {
        Map<String, ObjectNode> objectNodes = new HashMap<>();

        logger.debug("createInputSchema {}", method);

        final Type[] types = method.getParameterTypes();

        for (int i = 0; i < types.length; i++) {
            Annotation[] paramAns = method.getInvokedMethod().getParameterAnnotations()[i];
            Optional<Media> media = Optional.empty();

            boolean ignore = false;
            Optional<String> name = Optional.empty();
            for (Annotation paramAn : paramAns) {
                if (paramAn instanceof QueryParam) {
                    ignore = true;
                } else if (paramAn instanceof PathParam) {
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
                final ObjectContext.Builder objectContextBuilder = ObjectContext.buildFor(
                        GenericType.of(types[i]));

                if (method.hasAllowedValues(i)) {
                    final List<Object> allowedValues = method.getAllowedValues(i);
                    objectContextBuilder.withAllowedValues(allowedValues);
                }
                if (method.getParams() != null && method.getParams().length > i) {
                    objectContextBuilder.withCurrentValue(method.getParams()[i]);
                }

                if (method.hasDefaultValue(i)) {
                    objectContextBuilder.withDefaultValue(method.getDefaultValue(i));
                }

                final Optional<ObjectNode> objectNodeOption = generateJsonSchema(
                        objectContextBuilder.build(), createSchemaPropertyContext(method,
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

    private Optional<ObjectNode> generateJsonSchema(ObjectContext<?> objectContext,
            SchemaPropertyContext schemaPropertyContext) {
        final Property property = schemaPropertyGenerator.generateSchemaProperty(objectContext,
                schemaPropertyContext);
        if (property.getProperties().isEmpty() && property.getType() == PropertyType.OBJECT) {
            return Optional.empty();
        }
        return Optional.of(propertyJsonSchemaMapper.toJson(property));
    }

    private ObjectNode createObjectNode() {
        return new ObjectNode(new JsonNodeFactory(true));
    }
}
