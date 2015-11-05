package com.mercateo.common.rest.schemagen;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Field;

import com.mercateo.common.rest.schemagen.parameter.CallContext;
import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;

public class SchemaPropertyContext {

    private final CallContext callContext;

    private final FieldCheckerForSchema fieldCheckerForSchema;

    public SchemaPropertyContext(CallContext callContext,
            FieldCheckerForSchema fieldCheckerForSchema) {
        this.callContext = checkNotNull(callContext);
        this.fieldCheckerForSchema = checkNotNull(fieldCheckerForSchema);
    }

    public CallContext getCallContext() {
        return callContext;
    }

    public boolean isFieldApplicable(Field field) {
        checkNotNull(field);
        return fieldCheckerForSchema.test(field, callContext);
    }

}
