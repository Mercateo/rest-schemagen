package com.mercateo.common.rest.schemagen.util;

import com.fasterxml.jackson.annotation.JsonValue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.Stream;

public final class EnumUtil {
    private EnumUtil() {
    }

    public static String convertToString(Enum<? extends Enum<?>> x) {
        final Optional<Method> optionalValueMethod = Stream.of(x.getClass().getDeclaredMethods()).filter(
                m -> m.isAnnotationPresent(JsonValue.class)).findFirst();

        if (optionalValueMethod.isPresent()) {
            try {
                final Method valueMethod = optionalValueMethod.get();
                valueMethod.setAccessible(true);
                return valueMethod.invoke(x).toString();
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else {
            return x.name();
        }
    }
}
