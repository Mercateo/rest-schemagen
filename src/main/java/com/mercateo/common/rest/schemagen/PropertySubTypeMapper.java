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
package com.mercateo.common.rest.schemagen;

import com.mercateo.common.rest.schemagen.generictype.GenericType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

public final class PropertySubTypeMapper {

    private PropertySubTypeMapper() {
    }

    public static PropertySubType of(GenericType<?> type, PropertyType propertyType) {
        switch (propertyType) {
            case OBJECT:
                final Class<?> clazz = type.getRawType();
                if (Map.class.isAssignableFrom(clazz)) {
                    final ParameterizedType parameterizedType = (ParameterizedType) type.getType();
                    final Type type1 = parameterizedType.getActualTypeArguments()[0];
                    if (type1 instanceof Class && Enum.class.isAssignableFrom((Class) type1)) {
                        return PropertySubType.DICT;
                    }
                }
                break;
                default:

        }
        return PropertySubType.NONE;
    }
}
