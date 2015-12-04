package com.mercateo.common.rest.schemagen.parameter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class CallContextTest {

    private CallContext context;

    @Before
    public void setUp() {
        context = new CallContext();
    }

    @Test
    public void testViewClasses() {
        assertThat(context.getAdditionalObjectsFor(Class.class).isPresent()).isFalse();

        context.addAdditionalObjects(Class.class, getClass());
        context.addAdditionalObjects(Class.class, getClass());

        assertThat(context.getAdditionalObjectsFor(Class.class).get()).isNotEmpty();
    }
}