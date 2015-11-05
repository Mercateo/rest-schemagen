package com.mercateo.common.rest.schemagen.types;

import java.util.List;

public class PaginatedList<T> {
    public final List<T> members;

    public final int total;

    public final int offset;

    public final int limit;

    public PaginatedList(int total, int offset, int limit, List<T> members) {
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
