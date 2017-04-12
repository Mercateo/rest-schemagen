package com.mercateo.common.rest.schemagen.link;

import com.mercateo.common.rest.schemagen.parameter.CallContext;

import java.lang.reflect.Method;
import java.util.Optional;

public class CallScope extends Scope {
    private final Optional<CallContext> callContext;

    public CallScope(Class<?> clazz, Method method, Object[] params, CallContext callContext) {
        super(clazz, method, params);
        this.callContext = Optional.ofNullable(callContext);
    }

    public Optional<CallContext> getCallContext() {
        return callContext;
    }

    @Override
    public String toString() {
        return "CallScope{" +
                getInvokedClass().getName() + "." + getInvokedMethod().getName() + ", " +
                "callContext=" + callContext.orElse(null) +
                '}';
    }
}
