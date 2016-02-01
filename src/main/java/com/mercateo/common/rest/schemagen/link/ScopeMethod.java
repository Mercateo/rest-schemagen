package com.mercateo.common.rest.schemagen.link;

import java.lang.reflect.Method;

/**
 * @deprecated please use {@link Scope} instead
 */
public abstract class ScopeMethod extends Scope {
    public ScopeMethod(Class<?> clazz, Method method, Object[] params) {
        super(clazz, method, params);
    }
}
