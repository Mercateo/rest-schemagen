package com.mercateo.common.rest.schemagen.parameter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CallContextTest {

    private CallContext context;

    @BeforeEach
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