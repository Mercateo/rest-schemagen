package com.mercateo.common.rest.schemagen.link.helper;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ProxyFactoryTest {

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

    @Test
    public void testToStringPassThrough() {
        A a = ProxyFactory.createProxy(A.class);

        assertThat(a.toString()).isEqualTo("MethodInterceptor(A)");
    }

    @Test
    public void testPrimitiveReturnTypes() {
        final PrimitiveReturnTypes proxy = ProxyFactory.createProxy(PrimitiveReturnTypes.class);

        assertThat(proxy.getByte()).isEqualTo((byte)0);
        assertThat(proxy.getShort()).isEqualTo((short)0);
        assertThat(proxy.getInt()).isEqualTo(0);
        assertThat(proxy.getLong()).isEqualTo(0L);
        assertThat(proxy.getFloat()).isEqualTo(0.0f);
        assertThat(proxy.getDouble()).isEqualTo(0.0);
        assertThat(proxy.getBoolean()).isEqualTo(false);
        assertThat(proxy.getChar()).isEqualTo('\0');
        proxy.getVoid();
    }

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

    public static class PrimitiveReturnTypes {
        public byte getByte() {
            return 5;
        }

        public short getShort() {
            return 6;
        }

        public int getInt() {
            return 7;
        }

        public long getLong() {
            return 8;
        }

        public float getFloat() {
            return 3.14f;
        }

        public double getDouble() {
            return 3.1415;
        }

        public boolean getBoolean() {
            return true;
        }

        public char getChar() {
            return 't';
        }

        public void getVoid() {
        }
    }

}
