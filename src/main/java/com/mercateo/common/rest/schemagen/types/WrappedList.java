package com.mercateo.common.rest.schemagen.types;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WrappedList<T> {

    public final List<T> members;

    @JsonCreator
    public WrappedList(@JsonProperty("members") List<T> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "WrappedList [members=" + members + "]";
    }
}
