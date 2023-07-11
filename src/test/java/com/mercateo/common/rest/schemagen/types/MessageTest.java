package com.mercateo.common.rest.schemagen.types;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;

@ExtendWith(MockitoExtension.class)
public class MessageTest {

    @Mock
    private MessageData data;

    private MessageCode code;

    @BeforeEach
    public void setUp() {
        code = new MessageCode() {
            @Override
            public MessageType getType() {
                return MessageType.INFO;
            }

            @Override
            public String name() {
                return "<code>";
            }
        };
    }

    @Test
    public void testWithoutMessageData() {
        final Message message = Message.create(code);

        assertThat(message.getCode()).isEqualTo(code.name());
        assertThat(message.getType()).isEqualTo("INFO");
        assertThat(message.getData()).isNull();
    }

    @Test
    public void testWithMessageData() {
        final Message message = Message.create(code, data);

        assertThat(message.getCode()).isEqualTo(code.name());
        assertThat(message.getType()).isEqualTo("INFO");
        assertThat(message.getData()).isSameAs(data);
    }

    @Test
    public void testMultipleMessageDataShouldThrow() {
        assertThatThrownBy(() -> Message.create(code, data, data)) //
            .isExactlyInstanceOf(IllegalArgumentException.class) //
            .hasMessage("cannot handle more than one message data record");
    }

    static class Payload implements MessageData {
        public String value;
    }

    @Test
    public void shouldSerialize() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();

        final Payload payload = new Payload();
        payload.value = "<data>";
        final Message message = Message.create(code, payload);

        final String jsonString = objectMapper.writeValueAsString(message);

        assertThat(jsonString).isEqualTo("{\"type\":\"INFO\",\"code\":\"<code>\",\"data\":{\"value\":\"<data>\"}}");
    }

    @Test
    public void shouldDeserialize() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.deactivateDefaultTyping();

        final String content = "{\"type\":\"INFO\",\"code\":\"<code>\",\"data\":{\"value\":\"<data>\"}}";

        final Message message = mapper.readValue(content, Message.class);

        assertThat(message.getCode()).isEqualTo("<code>");
        assertThat(message.getType()).isEqualTo("INFO");
        // noinspection unchecked
        assertThat((Map<String, Object>) message.getData()).containsExactly(entry("value", "<data>"));
    }
}