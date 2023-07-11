/**
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

import com.googlecode.gentyref.GenericTypeReflector;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

public class GenericArray<T> extends GenericType<T> {
    private final GenericArrayType arrayType;

    public GenericArray(GenericArrayType arrayType, Class<T> rawType) {
        super(rawType);
        this.arrayType = arrayType;
    }

    @Override
    public String getSimpleName() {
        return arrayType.getTypeName();
    }

    @Override
    public Type getType() {
        return arrayType;
    }

    @Override
    public GenericType<?> getContainedType() {
        return of(GenericTypeReflector.getArrayComponentType(arrayType), getRawType()
                .getComponentType());
    }

    @Override
    public GenericType<? super T> getSuperType() {
        return null;
    }

    @Override
    public boolean isIterable() {
        return true;
    }
}
