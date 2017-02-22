package com.mercateo.common.rest.schemagen.json.mapper;

import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mercateo.common.rest.schemagen.JsonProperty;

class IntegerJsonPropertyMapper extends PrimitiveJsonPropertyMapperAbstract {

    IntegerJsonPropertyMapper(JsonNodeFactory nodeFactory) {
        super(nodeFactory);
    }

    @Override
    public ObjectNode toJson(JsonProperty jsonProperty) {
        final ObjectNode propertyNode = createObjectNode();
        propertyNode.put("type", "integer");
        addDefaultAndAllowedValues(propertyNode, jsonProperty, value -> new IntNode((Integer) value));
        jsonProperty.getValueConstraints().getMin().ifPresent(x -> propertyNode.put("minimum", x));
        jsonProperty.getValueConstraints().getMax().ifPresent(x -> propertyNode.put("maximum", x));
        return propertyNode;
    }
}
