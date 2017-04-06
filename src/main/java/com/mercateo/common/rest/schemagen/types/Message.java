package com.mercateo.common.rest.schemagen.types;

import static com.google.common.base.Preconditions.checkArgument;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class Message {
    private final String type;

    private final String code;

    private final Object data;

    @JsonCreator
    private Message(@JsonProperty("type") String type, @JsonProperty("code") String code,
            @JsonProperty("data") Object data) {
        this.type = type;
        this.code = code;
        this.data = data;
    }

    public static Message create(MessageCode messageCode, MessageData... messageData) {
        checkArgument(messageData.length <= 1, "cannot handle more than one message data record");

        return new Message(messageCode.getType().name(), messageCode.name(), messageData.length == 0 ? null
                : messageData[0]);
    }

    @JsonGetter("type")
    public String getType() {
        return type;
    }

    @JsonGetter("code")
    public String getCode() {
        return code;
    }

    @JsonGetter("data")
    public Object getData() {
        return data;
    }
}
