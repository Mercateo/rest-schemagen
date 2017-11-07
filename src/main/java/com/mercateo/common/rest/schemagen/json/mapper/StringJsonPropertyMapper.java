package com.mercateo.common.rest.schemagen.json.mapper;

import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.mercateo.common.rest.schemagen.JsonProperty;

class StringJsonPropertyMapper implements JsonPropertyMapper {

    private final PrimitiveJsonPropertyBuilder primitiveJsonPropertyBuilder;

    StringJsonPropertyMapper(JsonNodeFactory nodeFactory) {
        primitiveJsonPropertyBuilder = new PrimitiveJsonPropertyBuilder(nodeFactory);
    }

    @Override
    public ObjectNode toJson(JsonProperty jsonProperty) {
        Function<Object,JsonNode> nodeCreator = value -> new TextNode((String) value);
        final ObjectNode propertyNode = primitiveJsonPropertyBuilder.forProperty(jsonProperty).withType("string").withDefaultAndAllowedValues(nodeCreator).build();
        jsonProperty.getSizeConstraints().getMin().ifPresent(x -> propertyNode.put("minLength", x));
        jsonProperty.getSizeConstraints().getMax().ifPresent(x -> propertyNode.put("maxLength", x));
		if (jsonProperty.getFormat() != null) {
			propertyNode.put("format", jsonProperty.getFormat());
		}
		if (jsonProperty.getPattern() != null) {
			propertyNode.put("pattern", jsonProperty.getPattern());
		}
        return propertyNode;
    }
}
