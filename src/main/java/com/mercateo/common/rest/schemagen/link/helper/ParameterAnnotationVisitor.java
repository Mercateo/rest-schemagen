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
