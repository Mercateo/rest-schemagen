package com.mercateo.common.rest.schemagen.types;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.mercateo.common.rest.schemagen.JsonHyperSchema;
import org.junit.Test;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class WithIdTest {

    static class Payload {
        public String text;
        public Integer number;
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

        assertThat(payloadWithId.object.text).isEqualTo("foo");
        assertThat(payloadWithId.object.number).isEqualTo(123);
        assertThat(payloadWithId.id).isEqualTo(id);
    }

}