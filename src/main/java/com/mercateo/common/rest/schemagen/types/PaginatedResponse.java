package com.mercateo.common.rest.schemagen.types;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mercateo.common.rest.schemagen.JsonHyperSchema;

@JsonIgnoreProperties("object")
public class PaginatedResponse<T> extends ObjectWithSchema<PaginatedList<ObjectWithSchema<T>>> {

    @JsonIgnore
    public static final int DEFAULT_LIMIT = 2000;

    protected PaginatedResponse(@JsonProperty("members") List<ObjectWithSchema<T>> members,
            @JsonProperty("total") int total, @JsonProperty("offset") int offset, @JsonProperty("limit") int limit,
            @JsonProperty("_schema") JsonHyperSchema schema) {
        super(new PaginatedList<>(total, offset, limit, members), schema, null);
    }

    @Override
    public String toString() {
        return "PaginatedResponse [ payload=" + object.members + ", offset=" + object.offset + ", _schema=" + schema
                + "]";
    }

    public static <U> PaginatedResponse<U> create(List<ObjectWithSchema<U>> members, int total, int offset, int limit,
            JsonHyperSchema schema) {
        return new PaginatedResponse<>(members, total, offset, limit, schema);
    }

    public static <U> PaginatedResponse<U> create(PaginatedList<ObjectWithSchema<U>> paginatedList,
            JsonHyperSchema schema) {
        return new PaginatedResponse<>(paginatedList.members, paginatedList.total, paginatedList.offset,
                paginatedList.limit, schema);
    }

    public static <ElementIn, ElementOut> PaginatedResponseBuilder<ElementIn, ElementOut> builder() {
        return new PaginatedResponseBuilder<>();
    }

    @JsonProperty("members")
    public List<ObjectWithSchema<T>> getMembers() {
        return object.members;
    }

    @JsonProperty("offset")
    public Integer getOffset() {
        return object.offset;
    }

    @JsonProperty("limit")
    public Integer getLimit() {
        return object.limit;
    }

    @JsonProperty("total")
    public Integer getTotal() {
        return object.total;
    }
}
