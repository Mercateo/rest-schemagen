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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.mercateo.common.rest.schemagen.generictype.GenericType;

public final class PropertyTypeMapper {

    private static final Map<Class<?>, PropertyType> TYPE_MAP = new HashMap<>();

    static {
        TYPE_MAP.put(String.class, PropertyType.STRING);
        TYPE_MAP.put(Boolean.class, PropertyType.BOOLEAN);
        TYPE_MAP.put(boolean.class, PropertyType.BOOLEAN);
        TYPE_MAP.put(Integer.class, PropertyType.INTEGER);
        TYPE_MAP.put(int.class, PropertyType.INTEGER);
        TYPE_MAP.put(Long.class, PropertyType.INTEGER);
        TYPE_MAP.put(long.class, PropertyType.INTEGER);
		TYPE_MAP.put(Date.class, PropertyType.INTEGER);
        TYPE_MAP.put(Float.class, PropertyType.NUMBER);
        TYPE_MAP.put(float.class, PropertyType.NUMBER);
        TYPE_MAP.put(Double.class, PropertyType.NUMBER);
        TYPE_MAP.put(double.class, PropertyType.NUMBER);
        TYPE_MAP.put(BigInteger.class, PropertyType.INTEGER);
        TYPE_MAP.put(BigDecimal.class, PropertyType.NUMBER);
		TYPE_MAP.put(URL.class, PropertyType.STRING);
    }

    public static PropertyType of(GenericType<?> type) {
        if (type.isIterable()) {
            return PropertyType.ARRAY;
        }

        final Class<?> clazz = type.getRawType();

        if (Enum.class.isAssignableFrom(clazz)) {
            return PropertyType.STRING;
        }

        if (TYPE_MAP.containsKey(clazz)) {
            return TYPE_MAP.get(clazz);
        }

        return PropertyType.OBJECT;
    }

    public static PropertyType of(Class<?> clazz) {
        return of(GenericType.of(clazz));
    }
}
