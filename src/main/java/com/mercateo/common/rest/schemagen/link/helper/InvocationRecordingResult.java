package com.mercateo.common.rest.schemagen.link.helper;

import java.lang.reflect.Method;

public class InvocationRecordingResult {
    private final Method method;

    private final Object[] params;

    private final Class<?> invokedClass;

    InvocationRecordingResult(Method method, Object[] params, Class<?> invokedClass) {
        super();
        this.method = method;
        this.params = params;
        this.invokedClass = invokedClass;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getParams() {
        return params;
    }

    public Class<?> getInvokedClass() {
        return invokedClass;
    }
}
