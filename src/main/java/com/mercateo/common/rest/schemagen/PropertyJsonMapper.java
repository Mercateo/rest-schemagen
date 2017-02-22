package com.mercateo.common.rest.schemagen;

import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

class PropertyJsonMapper {

    private final JsonNodeFactory nodeFactory;

    PropertyJsonMapper(JsonNodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    void getBooleanDefaultAndAllowedValues(ObjectNode propertyNode, JsonProperty property) {
        propertyNode.set("default", BooleanNode.FALSE);
        addDefaultAndAllowedValues(propertyNode, property, this::getDefaultBooleanValue, this::getAllowedBooleanValues);
    }

    void addStringDefaultAndAllowedValues(ObjectNode propertyNode, JsonProperty property) {
        addDefaultAndAllowedValues(propertyNode, property, this::getDefaultStringValue, this::getAllowedStringValues);
    }

    void addIntegerDefaultAndAllowedValues(ObjectNode propertyNode, JsonProperty property) {
        addDefaultAndAllowedValues(propertyNode, property, this::getDefaultIntegerValue, this::getAllowedIntegerValues);
    }

    void addNumberDefaultAndAllowedValues(ObjectNode propertyNode, JsonProperty property) {
        addDefaultAndAllowedValues(propertyNode, property, this::getDefaultNumberValue, this::getAllowedNumberValues);
    }

    private void addDefaultAndAllowedValues(ObjectNode propertyNode, JsonProperty property, Function<JsonProperty, JsonNode> defaultValueNodeCreator, Function<JsonProperty, JsonNode> allowedValuesNodeCreator) {
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

    private ArrayNode getAllowedStringValues(JsonProperty jsonProperty) {
        return getAllowedValues(jsonProperty, n -> new TextNode((String) n));
    }

    private ArrayNode getAllowedIntegerValues(JsonProperty jsonProperty) {
        return getAllowedValues(jsonProperty, n -> new IntNode((Integer) n));
    }

    private ArrayNode getAllowedNumberValues(JsonProperty jsonProperty) {
        return getAllowedValues(jsonProperty, this::createNumberNode);
    }

    private ArrayNode getAllowedBooleanValues(JsonProperty jsonProperty) {
        return getAllowedValues(jsonProperty, this::createBooleanNode);
    }

    private ArrayNode getAllowedValues(JsonProperty jsonProperty, Function<Object, JsonNode> jsonNodeSupplier) {
        final ArrayNode arrayNode = new ArrayNode(nodeFactory);
        jsonProperty.getAllowedValues().stream().map(jsonNodeSupplier).forEach(arrayNode::add);
        return arrayNode;
    }

    private boolean hasDefaultValue(JsonProperty jsonProperty) {
        final Object defaultValue = jsonProperty.getDefaultValue();
        return defaultValue != null;
    }

    private TextNode getDefaultStringValue(JsonProperty jsonProperty) {
        return new TextNode((String)jsonProperty.getDefaultValue());
    }

    private IntNode getDefaultIntegerValue(JsonProperty jsonProperty) {
        return new IntNode((Integer) jsonProperty.getDefaultValue());
    }

    private JsonNode getDefaultNumberValue(JsonProperty jsonProperty) {
        return createNumberNode(jsonProperty.getDefaultValue());
    }

    private BooleanNode getDefaultBooleanValue(JsonProperty jsonProperty) {
        return createBooleanNode(jsonProperty.getDefaultValue());
    }

    private JsonNode createNumberNode(Object number) {
        if (number instanceof Float) {
            return new FloatNode((Float) number);
        } else if (number instanceof Double) {
            return new DoubleNode((Double) number);
        }
        return null;
    }

    private BooleanNode createBooleanNode(Object object) {
        return ((Boolean) object) ? BooleanNode.TRUE : BooleanNode.FALSE;
    }

}
