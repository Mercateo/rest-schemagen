package com.mercateo.common.rest.schemagen.types;

import java.util.List;

import com.mercateo.common.rest.schemagen.JsonHyperSchema;
import com.mercateo.common.rest.schemagen.WrappedList;

public class ListResponse<T> extends ObjectWithSchema<WrappedList<ObjectWithSchema<T>>> {

    protected ListResponse(List<ObjectWithSchema<T>> members, JsonHyperSchema schema) {
        super(new WrappedList<>(members), schema);
    }

    @Override
    public String toString() {
        return "ListResponseRto [ payload=" + object.members + ", _schema=" + schema + "]";
    }

    public static <T> ListResponse<T> create(List<ObjectWithSchema<T>> members, JsonHyperSchema schema) {
        return new ListResponse<>(members, schema);
    }
}
