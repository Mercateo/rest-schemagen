package com.mercateo.common.rest.schemagen.json.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mercateo.common.rest.schemagen.JsonProperty;

import java.util.function.Function;

class IntegerJsonPropertyMapper implements JsonPropertyMapper {

    private final PrimitiveJsonPropertyBuilder primitiveJsonPropertyBuilder;

    IntegerJsonPropertyMapper(JsonNodeFactory nodeFactory) {
        primitiveJsonPropertyBuilder = new PrimitiveJsonPropertyBuilder(nodeFactory);
    }

    @Override
    public ObjectNode toJson(JsonProperty jsonProperty) {
        Function<Object,JsonNode> nodeCreator = value -> new IntNode((Integer) value);
        final ObjectNode propertyNode = primitiveJsonPropertyBuilder.forProperty(jsonProperty)
                .withType("integer").withDefaultAndAllowedValues(nodeCreator).build();
        jsonProperty.getValueConstraints().getMin().ifPresent(x -> propertyNode.put("minimum", x));
        jsonProperty.getValueConstraints().getMax().ifPresent(x -> propertyNode.put("maximum", x));
        return propertyNode;
    }
}
