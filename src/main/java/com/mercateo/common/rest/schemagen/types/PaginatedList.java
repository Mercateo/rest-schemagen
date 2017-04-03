package com.mercateo.common.rest.schemagen.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.glassfish.jersey.server.JSONP;

import java.util.List;

public class PaginatedList<T> {
    public final List<T> members;

    public final int total;

    public final int offset;

    public final int limit;

    @JsonCreator
    public PaginatedList(@JsonProperty("total") int total, @JsonProperty("offset") int offset, @JsonProperty("limit") int limit, @JsonProperty("members") List<T> members) {
        this.total = total;
        this.offset = offset;
        this.limit = limit;
        this.members = members;
    }

    @Override
    public String toString() {
        return "PaginatedRto [total=" + total + ", offset=" + offset + ", limit=" + limit
                + ", members=" + members + "]";
    }

}
