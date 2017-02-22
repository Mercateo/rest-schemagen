package com.mercateo.common.rest.schemagen.json.mapper;

import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mercateo.common.rest.schemagen.JsonProperty;

abstract class PrimitiveJsonPropertyMapperAbstract implements JsonPropertyMapper {
    final private GenericJsonPropertyMapper genericJsonPropertyMapper;

    final private JsonNodeFactory nodeFactory;

    PrimitiveJsonPropertyMapperAbstract(JsonNodeFactory nodeFactory) {
        this.genericJsonPropertyMapper = new GenericJsonPropertyMapper(nodeFactory);
        this.nodeFactory = nodeFactory;
    }

    void addDefaultAndAllowedValues(ObjectNode propertyNode, JsonProperty jsonProperty,
            Function<Object, JsonNode> nodeCreator) {
        genericJsonPropertyMapper.addDefaultAndAllowedValues(propertyNode, jsonProperty, nodeCreator);
    }

    ObjectNode createObjectNode() {
        return new ObjectNode(nodeFactory);
    }
}
