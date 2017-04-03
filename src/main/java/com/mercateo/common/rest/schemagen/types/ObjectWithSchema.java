package com.mercateo.common.rest.schemagen.types;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.mercateo.common.rest.schemagen.IgnoreInRestSchema;
import com.mercateo.common.rest.schemagen.JsonHyperSchema;

public class ObjectWithSchema<T> {

    @JsonUnwrapped
    public final T object;

    @JsonProperty("_schema")
    @IgnoreInRestSchema
    public final JsonHyperSchema schema;

    @JsonProperty("_messages")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @IgnoreInRestSchema
    public final List<Message> messages;

    @JsonCreator
    protected ObjectWithSchema(@JsonProperty("object") T object, @JsonProperty("_schema") JsonHyperSchema schema,
            @JsonProperty("_messages") List<Message> messages) {
        // this has to be null, if T is Void, so please, do not "fix" this!
        this.object = object;
        this.schema = requireNonNull(schema);
        this.messages = messages != null ? messages : Collections.emptyList();
    }

    public static <U> ObjectWithSchema<U> create(U object, JsonHyperSchema schema) {
        return new ObjectWithSchema<>(object, schema, null);
    }

    @JsonIgnore
    public JsonHyperSchema getSchema() {
        return schema;
    }

    @JsonIgnore
    public List<Message> getMessages() {
        return messages;
    }

    @JsonIgnore
    public T getObject() {
        return object;
    }

    @Override
    public String toString() {
        return "ObjectWithSchema{" + "object=" + object + ", schema=" + schema + ", messages=" + messages + '}';
    }
}
