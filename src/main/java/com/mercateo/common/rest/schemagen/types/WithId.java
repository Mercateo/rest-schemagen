/*
 * Copyright © 2015 Mercateo AG (http://www.mercateo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mercateo.common.rest.schemagen.types;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Getter;
import lombok.Setter;

public class WithId<T> {

    @Getter
    @Setter
    private UUID id;

    @JsonUnwrapped
    @Getter
    @Setter
    private T object;

    @JsonCreator
    private WithId() {
    }

    private WithId(UUID id,T object) {
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
