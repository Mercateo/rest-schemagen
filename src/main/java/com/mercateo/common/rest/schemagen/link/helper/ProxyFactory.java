package com.mercateo.common.rest.schemagen.link.helper;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class ProxyFactory {
    private static void checkClassForFinalPublicMethods(Class<?> ct) {
        int classModifiers = ct.getModifiers();
        if (Modifier.isFinal(classModifiers)) {
            throw new IllegalStateException("The proxied class is not allowed to be final!");

        }
        Method[] methods = ct.getMethods();
        for (Method method : methods) {
            int modifiers = method.getModifiers();
            if (Modifier.isFinal(modifiers) && !method.getDeclaringClass().equals(Object.class)) {
                throw new IllegalStateException(
                        "The proxied class does not have to have any final public method!");
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Class<T> ct) {
        checkClassForFinalPublicMethods(ct);
        try {
            return (T) Enhancer.create(ct, new Class[] { InvocationRecorder.class },
                    new MethodInterceptor() {

                        private Map<Method, Supplier<?>> passThroughs = new HashMap<>();

                        {
                            passThroughs.put(InvocationRecorder.class.getMethod(
                                    "getInvocationRecordingResult"),
                                    () -> new InvocationRecordingResult(this.method, this.args,
                                            this.invokedClass));

                            passThroughs.put(Object.class.getMethod("toString"),
                                    () -> "MethodInterceptor(" + ct.getSimpleName() + ")");

                        }

                        private Method method;

                        private Object[] args;

                        private Class<T> invokedClass;

                        @Override
                        public Object intercept(Object obj, Method method, Object[] args,
                                MethodProxy proxy) {

                            if (passThroughs.containsKey(method)) {
                                return passThroughs.get(method).get();
                            }

                            this.method = method;
                            this.args = args;
                            this.invokedClass = ct;

                            return bogusReturn(method.getReturnType());
                        }

                        @SuppressWarnings("boxing")
                        private Object bogusReturn(Class<?> returnType) {
                            if (returnType.isPrimitive()) {
                                if (returnType.equals(byte.class)) {
                                    return (byte) 0;
                                } else if (returnType.equals(short.class)) {
                                    return (short) 0;
                                } else if (returnType.equals(int.class)) {
                                    return 0;
                                } else if (returnType.equals(long.class)) {
                                    return (long) 0;
                                } else if (returnType.equals(float.class)) {
                                    return (float) 0;
                                } else if (returnType.equals(double.class)) {
                                    return (double) 0;
                                } else if (returnType.equals(boolean.class)) {
                                    return false;
                                } else if (returnType.equals(char.class)) {
                                    return (char) 0;
                                } else if (returnType.equals(void.class)) {
                                    return null;
                                }
                                throw new IllegalArgumentException();
                            } else {
                                return null;
                            }
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException("Error creating proxy for class " + ct.getSimpleName(), e);
        }
    }
}
