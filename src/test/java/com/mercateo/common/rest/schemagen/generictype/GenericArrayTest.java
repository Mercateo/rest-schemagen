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
