package com.mercateo.common.rest.schemagen;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RestJsonSchemaGeneratorFactoryTest {

    @Test
    public void provideMethodShouldReturnSchemaGenerator() {
        assertThat(new RestJsonSchemaGeneratorFactory().provide()).isNotNull();
    }

}