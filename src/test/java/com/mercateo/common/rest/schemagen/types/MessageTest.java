package com.mercateo.common.rest.schemagen.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MessageTest {

    @Mock
    private MessageData data;

    private MessageCode code;

    @Before
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
}