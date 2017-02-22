package com.mercateo.common.rest.schemagen.json.mapper;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.mercateo.common.rest.schemagen.JsonProperty;
import com.mercateo.common.rest.schemagen.PropertyType;
import com.mercateo.common.rest.schemagen.generator.JsonPropertyResult;
import com.mercateo.common.rest.schemagen.plugin.IndividualSchemaGenerator;

class PropertyJsonSchemaMapperForRoot {
    private static final JsonNodeFactory nodeFactory = new JsonNodeFactory(true);

    private static final Map<PropertyType, JsonPropertyMapper> primitivePropertyMappers = ImmutableMap
        .<PropertyType, JsonPropertyMapper> builder()//
        .put(PropertyType.STRING, new StringJsonPropertyMapper(nodeFactory))
        .put(PropertyType.INTEGER, new IntegerJsonPropertyMapper(nodeFactory))
        .put(PropertyType.NUMBER, new NumberJsonPropertyMapper(nodeFactory))
        .put(PropertyType.BOOLEAN, new BooleanJsonPropertyMapper(nodeFactory))
        .build();

    private final Map<PropertyType, JsonPropertyMapper> objectPropertyMappers;

    private final Set<JsonProperty> referencedElements;

    PropertyJsonSchemaMapperForRoot(JsonPropertyResult jsonProperty) {
        this.referencedElements = jsonProperty.getReferencedElements();
        this.objectPropertyMappers = ImmutableMap
            .<PropertyType, JsonPropertyMapper> builder()//
            .put(PropertyType.OBJECT, new ObjectJsonPropertyMapper(this, nodeFactory))
            .put(PropertyType.ARRAY, new ArrayJsonPropertyMapper(this, nodeFactory))
            .build();
    }

    ObjectNode toJson(JsonProperty jsonProperty) {

        final Class<? extends IndividualSchemaGenerator> generator = jsonProperty.getIndividualSchemaGenerator();

        if (generator == null) {
            if (jsonProperty.getRef() != null) {
                final ObjectNode propertyNode = createObjectNode();
                propertyNode.put("$ref", jsonProperty.getRef());
                return propertyNode;
            } else {
                final JsonPropertyMapper jsonPropertyMapper;
                final PropertyType propertyType = jsonProperty.getType();

                if (primitivePropertyMappers.containsKey(propertyType)) {
                    jsonPropertyMapper = primitivePropertyMappers.get(propertyType);
                } else {
                    PropertyType objectPropertyType = objectPropertyMappers.containsKey(propertyType) ? propertyType
                            : PropertyType.OBJECT;
                    jsonPropertyMapper = objectPropertyMappers.get(objectPropertyType);
                }

                final ObjectNode propertyNode = jsonPropertyMapper.toJson(jsonProperty);
                if (referencedElements.contains(jsonProperty)) {
                    propertyNode.put("id", jsonProperty.getPath());
                }
                return propertyNode;
            }

        } else {
            final IndividualSchemaGenerator individualSchemaGenerator;

            try {
                individualSchemaGenerator = generator.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            return individualSchemaGenerator.create();
        }
    }

    private ObjectNode createObjectNode() {
        return new ObjectNode(nodeFactory);
    }

}
