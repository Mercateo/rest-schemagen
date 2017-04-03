package com.mercateo.common.rest.schemagen.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class WithId<T> {
    public final UUID id;

    @JsonUnwrapped
    public final T object;

    @JsonCreator
    private WithId(
            @JsonProperty("id") UUID id,
            @JsonProperty("object") T object
    ) {
        this.id = id;
        this.object = object;
    }

    public static <T> WithId<T> create(T payload) {
        return create(UUID.randomUUID(), payload);
    }

    public static <T> WithId<T> create(Map.Entry<UUID, T> entry) {
        return create(entry.getKey(), entry.getValue());
    }

    public static <T> WithId<T> create(UUID id, T payload) {
        return new WithId<>(id, payload);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WithId<?> withId = (WithId<?>) o;
        return Objects.equals(id, withId.id) &&
                Objects.equals(object, withId.object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, object);
    }

    @Override
    public String toString() {
        return "WithId{" +
                "id=" + id +
                ", object=" + object +
                '}';
    }
}
