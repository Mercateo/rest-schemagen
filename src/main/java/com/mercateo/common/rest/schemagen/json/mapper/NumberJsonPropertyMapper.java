package com.mercateo.common.rest.schemagen.json.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mercateo.common.rest.schemagen.JsonProperty;

class NumberJsonPropertyMapper extends PrimitiveJsonPropertyMapperAbstract {

    NumberJsonPropertyMapper(JsonNodeFactory nodeFactory) {
        super(nodeFactory);
    }

    @Override
    public ObjectNode toJson(JsonProperty jsonProperty) {
        final ObjectNode propertyNode = createObjectNode();
        propertyNode.put("type", "number");
        addDefaultAndAllowedValues(propertyNode, jsonProperty, this::createNode);
        return propertyNode;
    }

    private JsonNode createNode(Object value) {
        if (value instanceof Float) {
            return new FloatNode((Float) value);
        } else if (value instanceof Double) {
            return new DoubleNode((Double) value);
        }
        return null;
    }
}
