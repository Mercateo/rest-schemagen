package com.mercateo.common.rest.schemagen;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;

import java.util.function.Function;

class GenericJsonPropertyMapper {

    private final JsonNodeFactory nodeFactory;

    GenericJsonPropertyMapper(JsonNodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    void addDefaultAndAllowedValues(ObjectNode propertyNode, JsonProperty property, Function<JsonProperty, JsonNode> defaultValueNodeCreator, Function<JsonProperty, JsonNode> allowedValuesNodeCreator) {
        if (hasDefaultValue(property)) {
            propertyNode.set("default", defaultValueNodeCreator.apply(property));
        }
        if (hasAllowedValues(property)) {
            propertyNode.set("enum", allowedValuesNodeCreator.apply(property));
        }
    }

    private boolean hasAllowedValues(JsonProperty jsonProperty) {
        return !jsonProperty.getAllowedValues().isEmpty();
    }

    ArrayNode getAllowedValues(JsonProperty jsonProperty, Function<Object, JsonNode> jsonNodeSupplier) {
        final ArrayNode arrayNode = new ArrayNode(nodeFactory);
        jsonProperty.getAllowedValues().stream().map(jsonNodeSupplier).forEach(arrayNode::add);
        return arrayNode;
    }

    private boolean hasDefaultValue(JsonProperty jsonProperty) {
        final Object defaultValue = jsonProperty.getDefaultValue();
        return defaultValue != null;
    }

}
