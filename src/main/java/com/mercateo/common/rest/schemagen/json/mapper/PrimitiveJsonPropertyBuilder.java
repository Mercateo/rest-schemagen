/**
 * Copyright © 2015 Mercateo AG (http://www.mercateo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
