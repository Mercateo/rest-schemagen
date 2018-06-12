/**
 * Copyright © 2015 Mercateo AG (http://www.mercateo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
