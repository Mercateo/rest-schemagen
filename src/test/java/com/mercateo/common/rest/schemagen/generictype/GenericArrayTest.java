package com.mercateo.common.rest.schemagen.generictype;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import static org.assertj.core.api.Assertions.assertThat;

public class GenericArrayTest {

    class TestClass<T> {
        private T[] values;
    }

    private GenericType<?> genericType;

    @Before
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