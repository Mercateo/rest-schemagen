/*
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

import java.util.List;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mercateo.common.rest.schemagen.JsonProperty;

class ObjectJsonPropertyMapper implements JsonPropertyMapper {

    private final PropertyJsonSchemaMapperForRoot propertyJsonSchemaMapper;

    private final JsonNodeFactory nodeFactory;

    ObjectJsonPropertyMapper(PropertyJsonSchemaMapperForRoot propertyJsonSchemaMapper, JsonNodeFactory nodeFactory) {
        this.propertyJsonSchemaMapper = propertyJsonSchemaMapper;
        this.nodeFactory = nodeFactory;
    }

    public ObjectNode toJson(JsonProperty jsonProperty) {
        ObjectNode propertyNode = new ObjectNode(nodeFactory);
        propertyNode.put("type", "object");
        propertyNode.set("properties", createProperties(jsonProperty.getProperties()));
        final ArrayNode requiredElements = createRequiredElementsArray(jsonProperty.getProperties());
        if (requiredElements.size() > 0) {
            propertyNode.set("required", requiredElements);
        }
        return propertyNode;
    }

    private ObjectNode createProperties(List<JsonProperty> properties) {
        final ObjectNode result = new ObjectNode(nodeFactory);
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
