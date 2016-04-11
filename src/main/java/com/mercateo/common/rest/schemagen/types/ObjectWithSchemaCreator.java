package com.mercateo.common.rest.schemagen.types;

import com.mercateo.common.rest.schemagen.JsonHyperSchema;

public class ObjectWithSchemaCreator {

    public <T> ObjectWithSchema<T> create(T rto, JsonHyperSchema from) {
        return ObjectWithSchema.create(rto, from);
    }

}
