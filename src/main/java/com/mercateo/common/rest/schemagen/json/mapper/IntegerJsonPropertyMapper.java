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

import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mercateo.common.rest.schemagen.JsonProperty;

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
