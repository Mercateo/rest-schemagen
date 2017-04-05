package com.mercateo.common.rest.schemagen.types;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mercateo.common.rest.schemagen.JsonHyperSchema;

@JsonIgnoreProperties("object")
public class ListResponse<T> extends ObjectWithSchema<WrappedList<ObjectWithSchema<T>>> {

    @JsonCreator
    protected ListResponse(@JsonProperty("members") List<ObjectWithSchema<T>> members,
            @JsonProperty("_schema") JsonHyperSchema schema) {
        super(new WrappedList<>(members), schema, null);
    }

    @Override
    public String toString() {
        return "ListResponseRto [ payload=" + object.members + ", _schema=" + schema + "]";
    }

    public static <T> ListResponse<T> create(List<ObjectWithSchema<T>> members, JsonHyperSchema schema) {
        return new ListResponse<>(members, schema);
    }

    public static <ElementIn, ElementOut> ListResponseBuilder<ElementIn, ElementOut> builder() {
        // noinspection deprecation
        return new ListResponseBuilder<>();
    }

    @JsonProperty("members")
    public List<ObjectWithSchema<T>> getMembers() {
        return object.members;
    }
}
