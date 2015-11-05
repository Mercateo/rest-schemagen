package com.mercateo.common.rest.schemagen.link;

import static java.util.Objects.requireNonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import com.google.common.annotations.VisibleForTesting;
import com.googlecode.gentyref.GenericTypeReflector;
import com.mercateo.common.rest.schemagen.link.helper.InvocationRecordingResult;
import com.mercateo.common.rest.schemagen.parameter.CallContext;
import com.mercateo.common.rest.schemagen.parameter.Parameter;

public class ScopeMethod {
    private final Method invokedMethod;

    private final Object[] params;

    private final CallContext callContext;

    private final Class<?> invokedClass;

    public ScopeMethod(InvocationRecordingResult invocationRecordingResult) {
        this(invocationRecordingResult, CallContext.create());
    }

    public ScopeMethod(InvocationRecordingResult invocationRecordingResult,
            CallContext callContext) {
        this.invokedMethod = requireNonNull(invocationRecordingResult.getMethod());
        this.params = requireNonNull(invocationRecordingResult.getParams());
        this.invokedClass = requireNonNull(invocationRecordingResult.getInvokedClass());
        this.callContext = callContext;
    }

    @VisibleForTesting
    protected ScopeMethod(Method invokedMethod, Object[] params, Class<?> invokedClass) {
        this(invokedMethod, params, invokedClass, CallContext.create());
    }

    @VisibleForTesting
    protected ScopeMethod(Method invokedMethod, Object[] params, Class<?> invokedClass,
            CallContext callContext) {
        this.invokedMethod = requireNonNull(invokedMethod);
        this.params = requireNonNull(params);
        this.invokedClass = requireNonNull(invokedClass);
        this.callContext = callContext;
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

    public CallContext getCallContext() {
        return callContext;
    }

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
            if (callContext != null && callContext.hasParameter(argument)) {
                return map.apply(callContext.getParameter(argument));
            }
        }
        return negativeValue;
    }
}
