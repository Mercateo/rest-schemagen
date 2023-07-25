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
package com.mercateo.common.rest.schemagen;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.mercateo.common.rest.schemagen.generictype.GenericType;

public class GenericTypeTest {

    String plainString = "foo";

    List<String> simpleCollection = new ArrayList<>();

    @Test
    public void testGetClassForPlainType() {
        GenericType<?> genericType = getGenericTypeOfField("plainString");

        assertTrue(genericType.isInstanceOf(Object.class));
    }

    @Test
    public void testGetClassForSimpleCollection() {
        GenericType<?> genericType = getGenericTypeOfField("simpleCollection");

        assertTrue(genericType.isInstanceOf(List.class));
    }

    private GenericType<?> getGenericTypeOfField(String fieldName) {
        try {
            final Field declaredField = getClass().getDeclaredField(fieldName);
            return GenericType.of(declaredField.getGenericType(), declaredField.getType());
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

}