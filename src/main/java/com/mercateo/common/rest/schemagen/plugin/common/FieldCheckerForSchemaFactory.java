package com.mercateo.common.rest.schemagen.plugin.common;

import org.glassfish.hk2.api.Factory;

import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;

public class FieldCheckerForSchemaFactory implements Factory<FieldCheckerForSchema> {

    @Override
    public FieldCheckerForSchema provide() {
        return new JsonViewChecker();
    }

    @Override
    public void dispose(FieldCheckerForSchema instance) {
        // nothing
    }

}
