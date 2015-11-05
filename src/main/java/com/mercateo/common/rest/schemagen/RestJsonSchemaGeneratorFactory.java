package com.mercateo.common.rest.schemagen;

import org.glassfish.hk2.api.Factory;

public class RestJsonSchemaGeneratorFactory implements Factory<JsonSchemaGenerator> {
    @Override
    public JsonSchemaGenerator provide() {
        return new RestJsonSchemaGenerator();
    }

    @Override
    public void dispose(JsonSchemaGenerator jsonSchemaGenerator) {
        // Nothing to dispose.
    }
}
