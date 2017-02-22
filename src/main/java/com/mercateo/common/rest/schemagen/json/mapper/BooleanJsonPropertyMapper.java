package com.mercateo.common.rest.schemagen.json.mapper;

import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mercateo.common.rest.schemagen.JsonProperty;

class BooleanJsonPropertyMapper extends PrimitiveJsonPropertyMapperAbstract {

    BooleanJsonPropertyMapper(JsonNodeFactory nodeFactory) {
        super(nodeFactory);
    }

    @Override
    public ObjectNode toJson(JsonProperty jsonProperty) {
        final ObjectNode propertyNode = createObjectNode();
        propertyNode.put("type", "boolean");
        propertyNode.set("default", BooleanNode.FALSE);
        addDefaultAndAllowedValues(propertyNode, jsonProperty, value -> ((Boolean) value) ? BooleanNode.TRUE
                : BooleanNode.FALSE);
        return propertyNode;
    }
}
