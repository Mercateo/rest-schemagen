/*
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
/*
 * Created on 01.06.2015
 *
 * author joerg_adler
 */
package com.mercateo.common.rest.schemagen.link;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

public class BeanParamExtractor {
    private final static Logger logger = LoggerFactory.getLogger(BeanParamExtractor.class);

    public Map<String, Object[]> getQueryParameters(Object bean) {
        return getQueryParameters(bean, QueryParam.class, QueryParam::value, false).asMap()
                .entrySet().stream().map(this::transformAll).collect(
                        Collectors.toMap(Param::getName, Param::getValue, (a, b) -> a));
    }

    private Param<Object[]> transformAll(Map.Entry<String, Collection<Object>> entry) {
        return new Param<>(entry.getKey(), entry.getValue().stream().toArray());
    }

    public Map<String, Object> getPathParameters(Object bean) {
        return getQueryParameters(bean, PathParam.class, PathParam::value, true).asMap().entrySet()
                .stream().map(this::transformFirstOnly).collect(
                        Collectors.toMap(Param::getName, Param::getValue, (a, b) -> a));
    }

    private Param<Object> transformFirstOnly(Map.Entry<String, Collection<Object>> entry) {
        if (entry.getValue().size() != 1) {
            throw new IllegalStateException("No single occurence of a "
                    + "PathParam annotation for name " + entry.getKey());
        }
        return new Param<>(entry.getKey(), entry.getValue().stream().findFirst().get());
    }

    private <A extends Annotation> Multimap<String, Object> getQueryParameters(Object bean,
            Class<A> annotationClass, Function<A, String> parameterNameExtractor,
            boolean useTemplate) {
        Multimap<String, Object> result = ArrayListMultimap.create();
        if (bean != null) {
            @SuppressWarnings("unchecked")
            Set<Field> fields = ReflectionUtils.getAllFields(bean.getClass(), f -> f
                    .isAnnotationPresent(annotationClass));
            for (Field field : fields) {
                A annotation = field.getAnnotation(annotationClass);
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                    logger.trace("bean parameter field {} is not public", field.getName());
                }
                try {
                    String parameterName = parameterNameExtractor.apply(annotation);

                    Object parameterValue = field.get(bean);
                    if (parameterValue != null) {
                        if (parameterValue instanceof Iterable) {
                            result.putAll(parameterName, (Iterable<?>) parameterValue);
                        } else {
                            result.put(parameterName, parameterValue);
                        }
                    } else if (useTemplate) {
                        result.put(parameterName, "{" + parameterName + "}");
                    }
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        return result;
    }

    private static class Param<T> {
        private final String name;

        private final T value;

        public Param(String name, T value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public T getValue() {
            return value;
        }
    }
}
