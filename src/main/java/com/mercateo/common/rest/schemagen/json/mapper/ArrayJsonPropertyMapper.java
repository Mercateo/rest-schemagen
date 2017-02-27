package com.mercateo.common.rest.schemagen.json.mapper;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mercateo.common.rest.schemagen.JsonProperty;

class ArrayJsonPropertyMapper implements JsonPropertyMapper {

    private final PropertyJsonSchemaMapperForRoot propertyJsonSchemaMapper;

    private final JsonNodeFactory nodeFactory;

    ArrayJsonPropertyMapper(PropertyJsonSchemaMapperForRoot propertyJsonSchemaMapper, JsonNodeFactory nodeFactory) {
        this.propertyJsonSchemaMapper = propertyJsonSchemaMapper;
        this.nodeFactory = nodeFactory;
    }

    public ObjectNode toJson(JsonProperty jsonProperty) {
        ObjectNode propertyNode = new ObjectNode(nodeFactory);
        propertyNode.put("type", "array");
        propertyNode.set("items", propertyJsonSchemaMapper//
            .toJson(jsonProperty.getProperties().get(0)));
        jsonProperty
            .getSizeConstraints()
            .getMin()//
            .ifPresent(x -> propertyNode.put("minItems", x));
        jsonProperty
            .getSizeConstraints()
            .getMax() //
            .ifPresent(x -> propertyNode.put("maxItems", x));
        return propertyNode;
    }
}
