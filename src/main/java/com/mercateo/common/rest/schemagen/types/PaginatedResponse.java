package com.mercateo.common.rest.schemagen.types;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mercateo.common.rest.schemagen.JsonHyperSchema;

public class PaginatedResponse<T> extends ObjectWithSchema<PaginatedList<ObjectWithSchema<T>>> {

    @JsonIgnore
    public static final int DEFAULT_LIMIT = 2000;
    
    protected PaginatedResponse(List<ObjectWithSchema<T>> members, int total, int offset, int limit, JsonHyperSchema schema) {
        super(new PaginatedList<>(total, offset, limit, members), schema);
    }

    @Override
    public String toString() {
        return "PaginatedResponse [ payload=" + object.members + ", offset=" + object.offset + ", _schema=" + schema + "]";
    }

    public static <U> PaginatedResponse<U> create(List<ObjectWithSchema<U>> members, int total, int offset, int limit, JsonHyperSchema schema) {
        return new PaginatedResponse<>(members, total, offset, limit, schema);
    }

    public static <U> PaginatedResponse<U> create(PaginatedList<ObjectWithSchema<U>> paginatedList, JsonHyperSchema schema) {
        return new PaginatedResponse<>(paginatedList.members, paginatedList.total, paginatedList.offset, paginatedList.limit, schema);
    }

    public static <ElementIn, ElementOut> PaginatedResponseBuilder<ElementIn, ElementOut> builder() {
        return new PaginatedResponseBuilder<>();
    }
}
