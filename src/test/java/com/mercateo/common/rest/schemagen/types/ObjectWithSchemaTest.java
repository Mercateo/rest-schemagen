package com.mercateo.common.rest.schemagen.types;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;

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

    @Test
    public void shouldAddMessages() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();

        final Payload payload = new Payload();
        payload.value = "bar";
        payload.setNestedPayload(new NestedPayload("John"));
        final ObjectWithSchema<Payload> objectWithSchema = ObjectWithSchema.create(payload, JsonHyperSchema.from(
                Optional.empty()));
        MessageCode messageCode = new MessageCode() {
            @Override
            public MessageType getType() {
                return MessageType.INFO;
            }

            @Override
            public String name() {
                return "<code>";
            }
        };

        objectWithSchema.addMessage(Message.create(messageCode, new MessageData() {/**/}));

        final String jsonString = objectMapper.writeValueAsString(objectWithSchema);

        assertThat(jsonString).isEqualTo("{\"value\":\"bar\",\"name\":\"John\",\"_schema\":{\"links\":[]},\"_messages\":[{\"type\":\"INFO\",\"code\":\"<code>\",\"data\":{}}]}");

    }
}