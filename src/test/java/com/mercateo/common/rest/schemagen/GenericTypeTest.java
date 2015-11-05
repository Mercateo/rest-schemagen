package com.mercateo.common.rest.schemagen;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

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