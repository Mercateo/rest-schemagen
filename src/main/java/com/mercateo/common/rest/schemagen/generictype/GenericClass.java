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

import java.lang.reflect.Type;

import com.googlecode.gentyref.GenericTypeReflector;

public class GenericClass<T> extends GenericType<T> {

    public GenericClass(Class<T> type) {
        super(type);
    }

    @Override
    public String getSimpleName() {
        return getRawType().getSimpleName();
    }

    @Override
    public Type getType() {
        return getRawType();
    }

    @Override
    public GenericType<?> getContainedType() {
        if (getRawType().isArray()) {
            return GenericType.of(GenericTypeReflector.getArrayComponentType(getRawType()),
                    getRawType().getComponentType());
        }
        throw new IllegalAccessError("GenericClass " + getSimpleName() + " has no contained type");
    }

    @Override
    public GenericType<? super T> getSuperType() {
        final Class<? super T> superclass = getRawType().getSuperclass();
        return superclass != null ? GenericType.of(GenericTypeReflector.getExactSuperType(
                getRawType(), superclass), superclass) : null;
    }

    @Override
    public boolean isIterable() {
        return getRawType().isArray();
    }

    @Override
    public String toString() {
        return getRawType().toString();
    }
}
