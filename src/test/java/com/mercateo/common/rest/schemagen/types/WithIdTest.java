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

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class WithIdTest {

    static class Payload {
        public String text;
        public Integer number;
    }

    @Test
    public void shouldCreateRandomId() {
        final WithId<Payload> payloadWithId = WithId.create(new Payload());

        assertThat(payloadWithId.getId()).isNotNull();
    }

    @Test
    public void samePayloadWithDifferentIdIsNotEqual() throws Exception {
        final Payload payload = new Payload();
        final WithId<Payload> payloadWithId = WithId.create(payload);
        final WithId<Payload> otherPayloadWithId = WithId.create(payload);

        assertThat(payloadWithId).isNotEqualTo(otherPayloadWithId);
        assertThat(payloadWithId.hashCode()).isNotEqualTo(otherPayloadWithId.hashCode());
    }

    @Test
    public void samePayloadWithSameIdIsEqual() throws Exception {
        final Payload payload = new Payload();
        final UUID id = UUID.randomUUID();
        final WithId<Payload> payloadWithId = WithId.create(id, payload);
        final WithId<Payload> otherPayloadWithId = WithId.create(id, payload);

        assertThat(payloadWithId).isEqualTo(otherPayloadWithId);
        assertThat(payloadWithId.hashCode()).isEqualTo(otherPayloadWithId.hashCode());
    }

    @Test
    public void toStringShouldContainId() throws Exception {
        final Payload payload = new Payload();
        final UUID id = UUID.randomUUID();
        final WithId<Payload> payloadWithId = WithId.create(id, payload);

        assertThat(payloadWithId.toString()).contains(id.toString());
    }

    @Test
    public void shouldSerializeCorrectly() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();

        final Payload payload = new Payload();
        payload.text = "foo";
        payload.number = 123;

        final UUID id = UUID.randomUUID();
        final WithId<Payload> payloadWithId = WithId.create(id, payload);

        final String jsonString = objectMapper.writeValueAsString(payloadWithId);

        assertThat(jsonString).isEqualTo("{\"id\":\"" + id.toString() + "\",\"text\":\"foo\",\"number\":123}");
    }

    @Test
    public void shouldDeserializeCorrectly() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final TypeFactory typeFactory = mapper.getTypeFactory();

        UUID id = UUID.randomUUID();

        final String content = "{\"text\": \"foo\", \"number\": 123, \"id\": \"" + id.toString() + "\"}";

        final JavaType nameWithSchemaType = typeFactory.constructParametricType(WithId.class, Payload.class);
        final WithId<Payload> payloadWithId = mapper.readValue(content, nameWithSchemaType);

        assertThat(payloadWithId.getObject().text).isEqualTo("foo");
        assertThat(payloadWithId.getObject().number).isEqualTo(123);
        assertThat(payloadWithId.getId()).isEqualTo(id);
    }

}
