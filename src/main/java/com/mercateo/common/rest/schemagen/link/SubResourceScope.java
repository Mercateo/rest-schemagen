package com.mercateo.common.rest.schemagen.link;

import com.mercateo.common.rest.schemagen.parameter.CallContext;

import java.lang.reflect.Method;
import java.util.Optional;

public class SubResourceScope extends Scope {

    public SubResourceScope(Class<?> clazz, Method method, Object[] params) {
        super(clazz, method, params);
    }

    @Override
    public Optional<CallContext> getCallContext() {
        return Optional.empty();
    }
}
