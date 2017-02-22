package com.mercateo.common.rest.schemagen.json.mapper;

import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mercateo.common.rest.schemagen.JsonProperty;

class GenericJsonPropertyMapper {

    private final JsonNodeFactory nodeFactory;

    GenericJsonPropertyMapper(JsonNodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    void addDefaultAndAllowedValues(ObjectNode propertyNode, JsonProperty property,
            Function<Object, JsonNode> nodeCreator) {
        if (hasDefaultValue(property)) {
            propertyNode.set("default", getDefaultValue(property, nodeCreator));
        }
        if (hasAllowedValues(property)) {
            propertyNode.set("enum", getAllowedValues(property, nodeCreator));
        }
    }

    private boolean hasAllowedValues(JsonProperty jsonProperty) {
        return !jsonProperty.getAllowedValues().isEmpty();
    }

    private ArrayNode getAllowedValues(JsonProperty jsonProperty, Function<Object, JsonNode> nodeCreator) {
        final ArrayNode arrayNode = new ArrayNode(nodeFactory);
        jsonProperty.getAllowedValues().stream().map(nodeCreator).forEach(arrayNode::add);
        return arrayNode;
    }

    private boolean hasDefaultValue(JsonProperty jsonProperty) {
        final Object defaultValue = jsonProperty.getDefaultValue();
        return defaultValue != null;
    }

    private JsonNode getDefaultValue(JsonProperty jsonProperty, Function<Object, JsonNode> nodeCreator) {
        return nodeCreator.apply(jsonProperty.getDefaultValue());
    }

}
