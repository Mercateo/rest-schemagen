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
package com.mercateo.common.rest.schemagen.link;

import static java.util.Objects.requireNonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.googlecode.gentyref.GenericTypeReflector;
import com.mercateo.common.rest.schemagen.parameter.CallContext;
import com.mercateo.common.rest.schemagen.parameter.Parameter;

public abstract class Scope {
    private final Class<?> invokedClass;

    private final Method invokedMethod;

    private final Object[] params;

    public Scope(Class<?> clazz, Method method, Object[] params) {
        this.invokedClass = requireNonNull(clazz);
        this.invokedMethod = requireNonNull(method);
        this.params = requireNonNull(params);
    }

    public Method getInvokedMethod() {
        return invokedMethod;
    }

    public Object[] getParams() {
        return params;
    }

    public Class<?> getInvokedClass() {
        return invokedClass;
    }

    public Type[] getParameterTypes() {
        return GenericTypeReflector.getExactParameterTypes(invokedMethod, invokedClass);
    }

    public Type getReturnType() {
        return GenericTypeReflector.getExactReturnType(invokedMethod, invokedClass);
    }

    public Annotation[] getAnnotations() {
        return invokedMethod.getAnnotations();
    }

    public String getName() {
        return invokedMethod.getName();
    }

    public abstract Optional<CallContext> getCallContext();

    @SuppressWarnings("boxing")
    public boolean hasAllowedValues(int argumentIndex) {
        return getValues(argumentIndex, Parameter::hasAllowedValues, false);
    }

    @SuppressWarnings("unchecked")
    public List<Object> getAllowedValues(int argumentIndex) {
        return getValues(argumentIndex, p -> (List<Object>) p.getAllowedValues(), Collections
                .emptyList());
    }

    @SuppressWarnings("boxing")
    public boolean hasDefaultValue(int argumentIndex) {
        return getValues(argumentIndex, Parameter::hasDefaultValue, true);
    }

    public Object getDefaultValue(int argumentIndex) {
        return getValues(argumentIndex, Parameter::getDefaultValue, null);
    }

    private <T> T getValues(int argumentIndex, Function<Parameter<?>, T> map, T negativeValue) {
        if (argumentIndex < params.length) {
            final Object argument = params[argumentIndex];
            return getCallContext()
                    .filter(callContext -> callContext.hasParameter(argument))
                    .map(callContext -> map.apply(callContext.getParameter(argument)))
                    .orElse(negativeValue);
        }
        return negativeValue;
    }
}
