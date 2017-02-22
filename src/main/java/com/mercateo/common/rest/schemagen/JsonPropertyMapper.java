package com.mercateo.common.rest.schemagen;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;

import java.util.List;

/**
 * Created by Dimitry Polivave on 22.02.17.
 */
interface JsonPropertyMapper {
    ObjectNode toJson(JsonProperty jsonProperty);
}

class StringJsonPropertyMapper implements JsonPropertyMapper{
    private final GenericJsonPropertyMapper genericJsonPropertyMapper;
    private final JsonNodeFactory nodeFactory;

    StringJsonPropertyMapper(JsonNodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
        this.genericJsonPropertyMapper = new GenericJsonPropertyMapper(nodeFactory);
    }
    private TextNode getDefaultStringValue(JsonProperty jsonProperty) {
        return new TextNode((String)jsonProperty.getDefaultValue());
    }

    private ArrayNode getAllowedStringValues(JsonProperty jsonProperty) {
        return genericJsonPropertyMapper.getAllowedValues(jsonProperty, n -> new TextNode((String) n));
    }

    @Override
    public ObjectNode toJson(JsonProperty jsonProperty) {
        final ObjectNode propertyNode = new ObjectNode(nodeFactory);
        propertyNode.put("type", "string");
        genericJsonPropertyMapper.addDefaultAndAllowedValues(propertyNode, jsonProperty, //
                this::getDefaultStringValue, this::getAllowedStringValues);
        jsonProperty.getSizeConstraints().getMin().ifPresent(x -> propertyNode.put(
                "minLength", x));
        jsonProperty.getSizeConstraints().getMax().ifPresent(x -> propertyNode.put(
                "maxLength", x));
        return propertyNode;

    }
}

class IntegerJsonPropertyMapper implements JsonPropertyMapper{
    private final JsonNodeFactory nodeFactory;
    private final GenericJsonPropertyMapper genericJsonPropertyMapper;

    IntegerJsonPropertyMapper(JsonNodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
        this.genericJsonPropertyMapper = new GenericJsonPropertyMapper(nodeFactory);
    }
    private ArrayNode getAllowedIntegerValues(JsonProperty jsonProperty) {
        return genericJsonPropertyMapper.getAllowedValues(jsonProperty, n -> new IntNode((Integer) n));
    }

    private IntNode getDefaultIntegerValue(JsonProperty jsonProperty) {
        return new IntNode((Integer) jsonProperty.getDefaultValue());
    }

    @Override
    public ObjectNode toJson(JsonProperty jsonProperty) {
        final ObjectNode propertyNode = new ObjectNode(nodeFactory);
        propertyNode.put("type", "integer");
        genericJsonPropertyMapper.addDefaultAndAllowedValues(propertyNode, jsonProperty, //
                this::getDefaultIntegerValue, this::getAllowedIntegerValues);
        jsonProperty.getValueConstraints().getMin().ifPresent(x -> propertyNode.put("minimum",
                x));
        jsonProperty.getValueConstraints().getMax().ifPresent(x -> propertyNode.put("maximum",
                x));
        return propertyNode;
    }
}

class NumberJsonPropertyMapper implements JsonPropertyMapper{
    private final JsonNodeFactory nodeFactory;
    private final GenericJsonPropertyMapper genericJsonPropertyMapper;

    NumberJsonPropertyMapper(JsonNodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
        this.genericJsonPropertyMapper = new GenericJsonPropertyMapper(nodeFactory);
    }

    private ArrayNode getAllowedNumberValues(JsonProperty jsonProperty) {
        return genericJsonPropertyMapper.getAllowedValues(jsonProperty, this::createNumberNode);
    }

    private JsonNode getDefaultNumberValue(JsonProperty jsonProperty) {
        return createNumberNode(jsonProperty.getDefaultValue());
    }

    private JsonNode createNumberNode(Object number) {
        if (number instanceof Float) {
            return new FloatNode((Float) number);
        } else if (number instanceof Double) {
            return new DoubleNode((Double) number);
        }
        return null;
    }

    @Override
    public ObjectNode toJson(JsonProperty jsonProperty) {
        final ObjectNode propertyNode = new ObjectNode(nodeFactory);
        propertyNode.put("type", "number");
        genericJsonPropertyMapper.addDefaultAndAllowedValues(propertyNode, jsonProperty, this::getDefaultNumberValue, this::getAllowedNumberValues);
        return propertyNode;
    }
}

class BooleanJsonPropertyMapper implements JsonPropertyMapper{
    private final JsonNodeFactory nodeFactory;
    private final GenericJsonPropertyMapper genericJsonPropertyMapper;

    BooleanJsonPropertyMapper(JsonNodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
        this.genericJsonPropertyMapper = new GenericJsonPropertyMapper(nodeFactory);
    }

    private BooleanNode createBooleanNode(Object object) {
        return ((Boolean) object) ? BooleanNode.TRUE : BooleanNode.FALSE;
    }

    private ArrayNode getAllowedBooleanValues(JsonProperty jsonProperty) {
        return genericJsonPropertyMapper.getAllowedValues(jsonProperty, this::createBooleanNode);
    }

    private BooleanNode getDefaultBooleanValue(JsonProperty jsonProperty) {
        return createBooleanNode(jsonProperty.getDefaultValue());
    }

    @Override
    public ObjectNode toJson(JsonProperty jsonProperty) {
        final ObjectNode propertyNode = new ObjectNode(nodeFactory);
        propertyNode.put("type", "boolean");
        propertyNode.set("default", BooleanNode.FALSE);
        genericJsonPropertyMapper.addDefaultAndAllowedValues(propertyNode, jsonProperty,
                this::getDefaultBooleanValue, this::getAllowedBooleanValues);
        return propertyNode;
    }
}

class ObjectJsonPropertyMapper implements JsonPropertyMapper {
    private final PropertyJsonSchemaMapperForRoot propertyJsonSchemaMapper;
    private final JsonNodeFactory nodeFactory;

    ObjectJsonPropertyMapper(PropertyJsonSchemaMapperForRoot propertyJsonSchemaMapper, JsonNodeFactory nodeFactory) {
        this.propertyJsonSchemaMapper = propertyJsonSchemaMapper;
        this.nodeFactory = nodeFactory;
    }

    public ObjectNode toJson(JsonProperty jsonProperty) {
        ObjectNode propertyNode = createObjectNode();
        propertyNode.put("type", "object");
        propertyNode.set("properties", createProperties(jsonProperty.getProperties()));
        final ArrayNode requiredElements = createRequiredElementsArray(jsonProperty
                .getProperties());
        if (requiredElements.size() > 0) {
            propertyNode.set("required", requiredElements);
        }
        return propertyNode;
    }

    private ObjectNode createObjectNode() {
        return new ObjectNode(nodeFactory);
    }

    private ObjectNode createProperties(List<JsonProperty> properties) {
        final ObjectNode result = createObjectNode();
        for (JsonProperty jsonProperty : properties) {
            result.set(jsonProperty.getName(), propertyJsonSchemaMapper.toJson(jsonProperty));
        }
        return result;
    }

    private ArrayNode createRequiredElementsArray(List<JsonProperty> properties) {
        final ArrayNode result = new ArrayNode(nodeFactory);
        for (JsonProperty jsonProperty : properties) {
            if (jsonProperty.isRequired()) {
                result.add(jsonProperty.getName());
            }
        }
        return result;
    }
}

class ArrayJsonPropertyMapper implements JsonPropertyMapper {
    private final PropertyJsonSchemaMapperForRoot propertyJsonSchemaMapper;
    private final JsonNodeFactory nodeFactory;

    ArrayJsonPropertyMapper(PropertyJsonSchemaMapperForRoot propertyJsonSchemaMapper, JsonNodeFactory nodeFactory) {
        this.propertyJsonSchemaMapper = propertyJsonSchemaMapper;
        this.nodeFactory = nodeFactory;
    }

    public ObjectNode toJson(JsonProperty jsonProperty) {
        ObjectNode propertyNode = new ObjectNode(nodeFactory);
        propertyNode.put("type", "array");
        propertyNode.set("items", propertyJsonSchemaMapper//
                .toJson(jsonProperty.getProperties().get(0)));
        jsonProperty.getSizeConstraints().getMin()//
                .ifPresent(x -> propertyNode.put("minItems", x));
        jsonProperty.getSizeConstraints().getMax() //
                .ifPresent(x -> propertyNode.put("maxItems",x));
        return propertyNode;
    }

}

