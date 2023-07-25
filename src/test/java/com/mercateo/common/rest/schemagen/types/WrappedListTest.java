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

import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class WrappedListTest {

    static class Payload {
        public String value;
    }

    @Test
    public void shouldSerialize() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();

        final Payload payload = new Payload();
        payload.value = "foo";

        final WrappedList<Payload> wrappedList = new WrappedList<>(Collections.singletonList(payload));

        final String jsonString = objectMapper.writeValueAsString(wrappedList);

        assertThat(jsonString).isEqualTo("{\"members\":[{\"value\":\"foo\"}]}");
    }

    @Test
    public void shouldDeserialize() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final TypeFactory typeFactory = mapper.getTypeFactory();

        final String content = "{\"members\": [{\"value\": \"foo\", \"_schema\":{\"links\":[]}}, {\"value\": \"bar\", \"_schema\":{\"links\":[]}}]}";

        final TypeReference typeReference = new TypeReference<WrappedList<ObjectWithSchema<ListResponseTest.Payload>>>() {
        };
        final WrappedList<ObjectWithSchema<ListResponseTest.Payload>> listResponse = (WrappedList<ObjectWithSchema<ListResponseTest.Payload>>) mapper.readValue(content, typeReference);

        assertThat(listResponse.members)
                .extracting(ObjectWithSchema::getObject)
                .extracting(p -> p.value)
                .containsExactly("foo", "bar");
    }

}
