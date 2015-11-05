package com.mercateo.common.rest.schemagen.generictype;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.googlecode.gentyref.GenericTypeReflector;

public class GenericParameterizedType<T> extends GenericType<T> {

    private final ParameterizedType type;

    GenericParameterizedType(ParameterizedType type, Class<T> rawType) {
        super(rawType);
        this.type = requireNonNull(type);
    }

    @Override
    public ParameterizedType getType() {
        return type;
    }

    @Override
    public String getSimpleName() {
        return getRawType().getSimpleName();
    }

    @Override
    public GenericType<?> getContainedType() {
        Type[] actualTypeArguments = type.getActualTypeArguments();
        if (actualTypeArguments.length > 1) {
            throw new IllegalStateException(type + " not supported for subtyping");
        }
        return GenericType.of(actualTypeArguments[0], getRawType());
    }

    @Override
    public GenericType<? super T> getSuperType() {
        final Class<? super T> superclass = getRawType().getSuperclass();
        return superclass != null ? GenericType.of(GenericTypeReflector.getExactSuperType(type,
                superclass), superclass) : null;
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
