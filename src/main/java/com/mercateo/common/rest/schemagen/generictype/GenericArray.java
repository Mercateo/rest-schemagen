package com.mercateo.common.rest.schemagen.generictype;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

import com.googlecode.gentyref.GenericTypeReflector;

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
