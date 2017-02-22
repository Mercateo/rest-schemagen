package com.mercateo.common.rest.schemagen;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.mercateo.common.rest.schemagen.generator.JsonPropertyResult;
import com.mercateo.common.rest.schemagen.plugin.IndividualSchemaGenerator;

import java.util.Map;
import java.util.Set;

public class PropertyJsonSchemaMapper {

    public PropertyJsonSchemaMapper() {
    }

    /**
     * Convert the property hierarchy given in {@code property} to a JSON
     * string.
     * 
     * @param jsonProperty
     *            property hierarchy to be converted
     * @return JSONObject representing the schema of the given property
     *         hierarchy
     */
    public ObjectNode toJson(JsonPropertyResult jsonProperty) {
        return new PropertyJsonSchemaMapperForRoot(jsonProperty).toJson(jsonProperty
                .getRoot());
    }

}

class PropertyJsonSchemaMapperForRoot {
    private static final JsonNodeFactory nodeFactory = new JsonNodeFactory(true);

    private static final Map<PropertyType, JsonPropertyMapper> primitivePropertyMappers = ImmutableMap
            .<PropertyType, JsonPropertyMapper>builder()//
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
                .<PropertyType, JsonPropertyMapper>builder()//
                .put(PropertyType.OBJECT, new ObjectJsonPropertyMapper(this, nodeFactory))
                .put(PropertyType.ARRAY, new ArrayJsonPropertyMapper(this, nodeFactory))
                .build();
    }

    ObjectNode toJson(JsonProperty jsonProperty) {

        final Class<? extends IndividualSchemaGenerator> generator = jsonProperty
                .getIndividualSchemaGenerator();

        if (generator == null) {
            if (jsonProperty.getRef() != null) {
                final ObjectNode propertyNode = createObjectNode();
                propertyNode.put("$ref", jsonProperty.getRef());
                return propertyNode;
            } else {
                final JsonPropertyMapper jsonPropertyMapper;
                if (primitivePropertyMappers.containsKey(jsonProperty.getType())) {
                    jsonPropertyMapper = primitivePropertyMappers.get(jsonProperty.getType());
                } else if (objectPropertyMappers.containsKey(jsonProperty.getType())) {
                    jsonPropertyMapper = objectPropertyMappers.get(jsonProperty.getType());
                } else {
                    jsonPropertyMapper = objectPropertyMappers.get(PropertyType.OBJECT);
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
