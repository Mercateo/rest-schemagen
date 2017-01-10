package com.mercateo.common.rest.schemagen.link;


import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LinkFactoryContextDefaultTest {
    @Test
    public void shouldBeInstantiableWithDefaultConstructor() throws Exception {
        final LinkFactoryContextDefault linkFactoryContext = new LinkFactoryContextDefault();

        assertThat(linkFactoryContext).isNotNull();
        assertThat(linkFactoryContext.getBaseUri()).isNull();
        assertThat(linkFactoryContext.getFieldCheckerForSchema()).isNull();
        assertThat(linkFactoryContext.getMethodCheckerForLink()).isNull();
    }
}