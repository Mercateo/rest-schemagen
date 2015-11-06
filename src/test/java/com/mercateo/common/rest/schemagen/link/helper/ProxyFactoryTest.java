package com.mercateo.common.rest.schemagen.link.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Method;

import org.junit.Test;

public class ProxyFactoryTest {

    public static class A {
        public int primitiveReturn() {
            return 1;
        }

        public String objectReturn() {
            return "in method";
        }
    }

    public static class B {
        public final int primitiveReturn() {
            return 1;
        }

        public String objectReturn() {
            return "in method";
        }
    }

    public static final class C {
        public int primitiveReturn() {
            return 1;
        }

        public String objectReturn() {
            return "in method";
        }
    }

    @Test
    public void testPrimitveMethod() throws NoSuchMethodException, SecurityException {
        A a = ProxyFactory.createProxy(A.class);
        assertEquals(0, a.primitiveReturn());
        Method method = ((InvocationRecorder) a).getInvocationRecordingResult().getMethod();
        assertEquals(A.class.getMethod("primitiveReturn"), method);
    }

    @Test
    public void testObjectMethod() throws NoSuchMethodException, SecurityException {
        A a = ProxyFactory.createProxy(A.class);
        assertNull(a.objectReturn());
        Method method = ((InvocationRecorder) a).getInvocationRecordingResult().getMethod();
        assertEquals(A.class.getMethod("objectReturn"), method);
    }

    @Test(expected = IllegalStateException.class)
    public void testFinalClassShouldBeRejected() {
        ProxyFactory.createProxy(C.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testFinalMethodsShouldBeRejected() {
        ProxyFactory.createProxy(B.class);
    }
}
