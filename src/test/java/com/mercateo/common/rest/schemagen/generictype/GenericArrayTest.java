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

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GenericArrayTest {

    class TestClass<T> {
        private T[] values;
    }

    private GenericType<?> genericType;

    @BeforeEach
    public void setUp() throws NoSuchFieldException {
        final Field field = TestClass.class.getDeclaredField("values");
        this.genericType = GenericType.of(field.getGenericType(), field.getType());
    }

    @Test
    public void getSimpleNameReturnsCorrectName() {
        assertThat(genericType.getSimpleName()).isEqualTo("T[]");
    }

    @Test
    public void getSupertypeAlwaysReturnsNull() {
        assertThat(genericType.getSuperType()).isNull();
    }

}
