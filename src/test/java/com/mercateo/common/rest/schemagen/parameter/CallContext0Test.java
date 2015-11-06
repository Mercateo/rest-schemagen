package com.mercateo.common.rest.schemagen.parameter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class CallContext0Test {

    private CallContext context;

    @Before
    public void setUp() {
        context = new CallContext();
    }

    @Test
    public void testViewClasses() {
        assertThat(context.getAddionalObjectsFor(Class.class).isPresent()).isFalse();

        context.addAddionalObjects(Class.class, getClass());

        assertThat(context.getAddionalObjectsFor(Class.class).get()).isNotEmpty();
    }
}