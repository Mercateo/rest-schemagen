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

        Optional<ObjectNode> beanParam = Optional.empty();

        for (int i = 0; i < types.length; i++) {
            final Type parameterType = types[i];
            Annotation[] parameterAnnotations = scope.getInvokedMethod().getParameterAnnotations()[i];

            Optional<Media> media = Optional.empty();
            boolean ignore = false;
            boolean isBeanParam = false;

            Optional<String> name = Optional.empty();
            for (Annotation parameterAnnotation : parameterAnnotations) {
                if (parameterAnnotation instanceof QueryParam) {
                    ignore = true;
                } else if (parameterAnnotation instanceof PathParam) {
                    ignore = true;
                } else if (parameterAnnotation instanceof BeanParam) {
                    isBeanParam = true;
                } else if (parameterAnnotation instanceof HeaderParam) {
                    ignore = true;
                } else if (parameterAnnotation instanceof FormDataParam) {
                    ignore = true;
                } else if (parameterAnnotation instanceof Context) {
                    ignore = true;
                } else if (parameterAnnotation instanceof FormParam) {
                    FormParam formParam = (FormParam) parameterAnnotation;
                    name = Optional.of(formParam.value());
                } else if (parameterAnnotation instanceof Media) {
                    media = Optional.of((Media) parameterAnnotation);
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
                if (!isBeanParam && objectNodes.containsKey(propertyName)) {
                    throw new IllegalStateException("multiple properties named <" + propertyName
                            + "> found");
                }
                if (isBeanParam) {
                    beanParam = Optional.of(objectNode);
                } else {
                    objectNodes.put(propertyName, objectNode);
                }
            }
        }

        beanParam.ifPresent(bp -> {
            if (!objectNodes.containsKey("")) {
                objectNodes.put("", bp);
            }
        });

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
