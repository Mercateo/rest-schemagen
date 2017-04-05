package com.mercateo.common.rest.schemagen.generictype;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.googlecode.gentyref.GenericTypeReflector;
import com.mercateo.common.rest.schemagen.JsonHyperSchema;
import com.mercateo.common.rest.schemagen.types.ObjectWithSchema;

public class GenericParameterizedTypeTest extends ObjectWithSchema<Boolean> {

    @SuppressWarnings("unused")
	private List<List<Double>> doubleList;

    @SuppressWarnings("boxing")
    public GenericParameterizedTypeTest() {
        super(true, new JsonHyperSchema(new ArrayList<>()), null);
    }

    @Test
    public void testGetContainedType() throws NoSuchFieldException {
        final Field field = getClass().getDeclaredField("doubleList");
        final Type type = GenericTypeReflector.getExactFieldType(field, getClass());

        @SuppressWarnings("rawtypes")
        final GenericParameterizedType<List> genericType = new GenericParameterizedType<>(
                (ParameterizedType) type, List.class);

        final GenericType<?> containedType1 = genericType.getContainedType();
        assertThat(containedType1.getRawType()).isEqualTo(List.class);

        final GenericType<?> containedType2 = containedType1.getContainedType();
        assertThat(containedType2.getRawType()).isEqualTo(Double.class);
    }

    @Test
    public void testGetContainedTypeWithGenericTypeParameter() throws NoSuchFieldException {
        final Class<?> superclass = getClass().getSuperclass();
        final Field field = superclass.getDeclaredField("object");
        final Type type = GenericTypeReflector.getExactFieldType(field, getClass());

        final GenericType<?> genericType = GenericType.of(type);

        assertThat(genericType.getRawType()).isEqualTo(Boolean.class);
    }

}