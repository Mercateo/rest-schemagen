package com.mercateo.common.rest.schemagen.types;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.mercateo.common.rest.schemagen.JsonHyperSchema;

public class ObjectWithSchemaTest {

    static class Payload {
        public String value;
    }

    @Test
    public void shouldSerialize() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();

        final Payload payload = new Payload();
        payload.value = "bar";
        final ObjectWithSchema<Payload> objectWithSchema = ObjectWithSchema.create(payload, JsonHyperSchema.from(
                Optional.empty()));

        final String jsonString = objectMapper.writeValueAsString(objectWithSchema);

        assertThat(jsonString).isEqualTo("{\"value\":\"bar\",\"_schema\":{\"links\":[]}}");
    }

    @Test
    public void shouldDeserialize() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final TypeFactory typeFactory = mapper.getTypeFactory();

        final String content = "{\"value\": \"foo\", \"_schema\": {\"links\": []}}";

        final JavaType nameWithSchemaType = typeFactory.constructParametricType(ObjectWithSchema.class, Payload.class);
        final ObjectWithSchema<Payload> objectWithSchema = mapper.readValue(content, nameWithSchemaType);

        assertThat(objectWithSchema.getObject().value).isEqualTo("foo");
        assertThat(objectWithSchema.getSchema().getLinks()).isEmpty();
        assertThat(objectWithSchema.getMessages()).isEmpty();
    }
}