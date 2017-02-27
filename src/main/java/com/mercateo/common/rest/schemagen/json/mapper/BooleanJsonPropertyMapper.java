package com.mercateo.common.rest.schemagen.json.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mercateo.common.rest.schemagen.JsonProperty;

import java.util.function.Function;

class BooleanJsonPropertyMapper implements JsonPropertyMapper {

    private final PrimitiveJsonPropertyBuilder primitiveJsonPropertyBuilder;

    BooleanJsonPropertyMapper(JsonNodeFactory nodeFactory) {
        primitiveJsonPropertyBuilder = new PrimitiveJsonPropertyBuilder(nodeFactory);
    }

    @Override
    public ObjectNode toJson(JsonProperty jsonProperty) {
        Function<Object,JsonNode> nodeCreator = value -> ((Boolean) value) ? BooleanNode.TRUE
                : BooleanNode.FALSE;
        return primitiveJsonPropertyBuilder.forProperty(jsonProperty)
                .withType("boolean")
                .withDefaultValue(BooleanNode.FALSE)
                .withDefaultAndAllowedValues(nodeCreator).build();
    }

}
