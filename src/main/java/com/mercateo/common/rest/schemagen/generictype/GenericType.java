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
package com.mercateo.common.rest.schemagen.generictype;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class GenericType<T> {

    private final Class<T> rawType;

    GenericType(Class<T> rawType) {
        this.rawType = requireNonNull(rawType);
    }

    public final Class<T> getRawType() {
        return rawType;
    }

    public abstract String getSimpleName();

    public abstract Type getType();

    public abstract GenericType<?> getContainedType();

    public boolean isInstanceOf(Class<?> clazz) {
        return requireNonNull(clazz).isAssignableFrom(getRawType());
    }

    public boolean isIterable() {
        return Iterable.class.isAssignableFrom(getRawType());
    }

    public static GenericType<?> of(Type type) {
        return of(type, null);
    }

    @SuppressWarnings("unchecked")
	public static <T> GenericType<T> of(Type type, Class<T> rawType) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return new GenericParameterizedType<>(parameterizedType, (Class<T>) parameterizedType
                    .getRawType());
        } else if (type instanceof Class) {
            return new GenericClass<>((Class<T>) type);
        } else if (type instanceof GenericArrayType) {
            return new GenericArray<>((GenericArrayType) type, requireNonNull(rawType));
        }
        {
            throw new IllegalStateException("unhandled type " + type);
        }
    }

    public Field[] getDeclaredFields() {
        return getRawType().getDeclaredFields();
    }

    public abstract GenericType<? super T> getSuperType();
}
