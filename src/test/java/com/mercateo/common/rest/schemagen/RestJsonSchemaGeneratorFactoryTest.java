package com.mercateo.common.rest.schemagen;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class RestJsonSchemaGeneratorFactoryTest {

    @Test
    public void provideMethodShouldReturnSchemaGenerator() {
        assertThat(new RestJsonSchemaGeneratorFactory().provide()).isNotNull();
    }

}
