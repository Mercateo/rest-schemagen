package com.mercateo.common.rest.schemagen;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import com.mercateo.common.rest.schemagen.generictype.GenericType;

public class FieldParameters {
    private final Map<Class<? extends Annotation>, Annotation> annotations;

    private final String name;

    private final GenericType<?> javaType;

    public FieldParameters(String name, GenericType<?> javaType, Annotation[] annotations) {
        this(name, javaType, new HashMap<>());

        for (Annotation annotation : annotations) {
            this.annotations.put(annotation.annotationType(), annotation);
        }
    }

    private FieldParameters(String name, GenericType<?> javaType,
            final Map<Class<? extends Annotation>, Annotation> annotations) {
        this.name = name;
        this.javaType = javaType;
        this.annotations = annotations;
    }

    public String getName() {
        return name;
    }

    public GenericType<?> getJavaType() {
        return javaType;
    }

    public boolean hasAnnontation(Class<? extends Annotation> annotationClass) {
        return annotations.containsKey(annotationClass);
    }

    @SuppressWarnings("unchecked")
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return (T) annotations.get(annotationClass);
    }

    public FieldParameters getGenericSubtype() {
        return new FieldParameters(name, javaType.getContainedType(), annotations);
    }

    @Override
    public String toString() {
        return "FieldParameters{" + "name='" + name + '\'' + ", javaType=" + javaType + '}';
    }
}
