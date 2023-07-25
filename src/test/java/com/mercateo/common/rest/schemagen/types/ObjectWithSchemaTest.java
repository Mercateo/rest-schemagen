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
package com.mercateo.common.rest.schemagen.types;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.mercateo.common.rest.schemagen.JsonHyperSchema;

import lombok.Data;
import lombok.Value;

public class ObjectWithSchemaTest {

    @Data
    static class Payload {
        public String value;

        @JsonUnwrapped
        public NestedPayload nestedPayload;
    }

    @Value
    static class NestedPayload {
        String name;
    }

    @Test
    public void shouldSerialize() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();

        final Payload payload = new Payload();
        payload.value = "bar";
        payload.setNestedPayload(new NestedPayload("John"));
        final ObjectWithSchema<Payload> objectWithSchema = ObjectWithSchema.create(payload, JsonHyperSchema.from(
                Optional.empty()));

        final String jsonString = objectMapper.writeValueAsString(objectWithSchema);

        assertThat(jsonString).isEqualTo("{\"value\":\"bar\",\"name\":\"John\",\"_schema\":{\"links\":[]}}");
    }

    @Test
    public void shouldDeserialize() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final TypeFactory typeFactory = mapper.getTypeFactory();

        final String content = "{\"value\":\"foo\",\"name\":\"John\",\"_schema\":{\"links\":[]}}";

        final JavaType nameWithSchemaType = typeFactory.constructParametricType(ObjectWithSchema.class, Payload.class);
        final ObjectWithSchema<Payload> objectWithSchema = mapper.readValue(content, nameWithSchemaType);

        assertThat(objectWithSchema.getObject().value).isEqualTo("foo");
        assertThat(objectWithSchema.getObject().getNestedPayload()).isEqualTo(new NestedPayload("John"));
        assertThat(objectWithSchema.getSchema().getLinks()).isEmpty();
        assertThat(objectWithSchema.getMessages()).isEmpty();
    }
}
