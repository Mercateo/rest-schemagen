/*
 * Created on 01.06.2015
 *
 * author joerg_adler
 */
package com.mercateo.common.rest.schemagen.link;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mercateo.common.rest.schemagen.link.helper.Pair;

public class BeanParamExtractor {
    private final static Logger logger = LoggerFactory.getLogger(BeanParamExtractor.class);

    public Map<String, Object[]> getQueryParameters(Object bean) {
        return getQueryParameters(bean, QueryParam.class, QueryParam::value, false).asMap()
                .entrySet().stream().map(e -> new Pair<>(e.getKey(), e.getValue().stream()
                        .toArray())).collect(Collectors.toMap(p -> p.left, p -> p.right, (a,
                                b) -> a));
    }

    public Map<String, Object> getPathParameters(Object bean) {
        return getQueryParameters(bean, PathParam.class, PathParam::value, true).asMap().entrySet()
                .stream().map(e -> {
                    if (e.getValue().size() != 1) {
                        throw new IllegalStateException("No single occurence of a "
                                + "PathParam annotation for name " + e.getKey());
                    }
                    return new Pair<>(e.getKey(), e.getValue().stream().findFirst().get());
                }).collect(Collectors.toMap(p -> p.left, p -> p.right, (a, b) -> a));
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
}
