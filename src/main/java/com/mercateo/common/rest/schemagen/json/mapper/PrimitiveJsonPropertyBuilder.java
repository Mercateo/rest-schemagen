package com.mercateo.common.rest.schemagen.json.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mercateo.common.rest.schemagen.JsonProperty;

import java.util.function.Function;

class PrimitiveJsonPropertyBuilder {
    final private GenericJsonPropertyMapper genericJsonPropertyMapper;

    final private JsonNodeFactory nodeFactory;

    PrimitiveJsonPropertyBuilder(JsonNodeFactory nodeFactory) {
        this.genericJsonPropertyMapper = new GenericJsonPropertyMapper(nodeFactory);
        this.nodeFactory = nodeFactory;
    }

    Builder forProperty(JsonProperty jsonProperty){
        return new Builder(jsonProperty);
    }

    class Builder{
        private ObjectNode propertyNode;
        private JsonProperty jsonProperty;

        Builder (JsonProperty jsonProperty) {
            this.jsonProperty = jsonProperty;
            propertyNode = new ObjectNode(nodeFactory);
        }

        Builder withType(String type) {
            propertyNode.put("type", type);
            return this;
        }

        Builder withDefaultAndAllowedValues(Function<Object, JsonNode> nodeCreator) {
            genericJsonPropertyMapper.addDefaultAndAllowedValues(propertyNode, this.jsonProperty, nodeCreator);
            return this;
        }

        Builder withDefaultValue(JsonNode value) {
            propertyNode.set("default", value);
            return this;
        }

        ObjectNode build() {
            return propertyNode;
        }

    }
}
