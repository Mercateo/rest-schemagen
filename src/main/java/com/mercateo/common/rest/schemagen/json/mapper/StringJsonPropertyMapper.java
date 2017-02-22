package com.mercateo.common.rest.schemagen.json.mapper;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.mercateo.common.rest.schemagen.JsonProperty;

class StringJsonPropertyMapper extends PrimitiveJsonPropertyMapperAbstract {

    StringJsonPropertyMapper(JsonNodeFactory nodeFactory) {
        super(nodeFactory);
    }

    @Override
    public ObjectNode toJson(JsonProperty jsonProperty) {
        final ObjectNode propertyNode = createObjectNode();
        propertyNode.put("type", "string");
        addDefaultAndAllowedValues(propertyNode, jsonProperty, value -> new TextNode((String) value));
        jsonProperty.getSizeConstraints().getMin().ifPresent(x -> propertyNode.put("minLength", x));
        jsonProperty.getSizeConstraints().getMax().ifPresent(x -> propertyNode.put("maxLength", x));
        return propertyNode;
    }
}
