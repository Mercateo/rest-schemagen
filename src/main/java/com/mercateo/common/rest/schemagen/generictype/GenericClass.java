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
