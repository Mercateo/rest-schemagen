package com.mercateo.common.rest.schemagen.types;

import java.util.List;

public class WrappedList<T> {

    public final List<T> members;

    public WrappedList(List<T> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "WrappedList [members=" + members + "]";
    }
}
