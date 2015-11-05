package com.mercateo.common.rest.schemagen.link.helper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class ParameterAnnotationVisitor {

    public static void visitAnnotations(Visitor visitor, Method method, Object... parameters) {
        visitAnnotations(visitor, method.getParameterAnnotations(), parameters);
    }

    public static void visitAnnotations(Visitor visitor, Annotation[][] parameterAnnotations,
            Object[] parameters) {
        int parameterIndex = 0;
        for (Annotation[] annotations : parameterAnnotations) {
            final Object parameter = parameterIndex < parameters.length ? parameters[parameterIndex]
                    : null;

            for (Annotation annotation : annotations) {
                visitor.visit(parameter, parameterIndex, annotation);
            }

            parameterIndex++;
        }
    }

    public static interface Visitor {
        void visit(Object parameter, int parameterIndex, Annotation annotation);
    }
}
