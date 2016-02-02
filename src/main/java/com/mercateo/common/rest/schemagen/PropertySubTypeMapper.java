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
        }
        return PropertySubType.NONE;
    }
}
