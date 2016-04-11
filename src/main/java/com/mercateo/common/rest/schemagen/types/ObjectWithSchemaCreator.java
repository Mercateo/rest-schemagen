package com.mercateo.common.rest.schemagen.types;

import com.mercateo.common.rest.schemagen.JsonHyperSchema;

public class ObjectWithSchemaCreator<T> {

    public ObjectWithSchema<T> create(T rto, JsonHyperSchema from) {
        return ObjectWithSchema.create(rto, from);
    }

}
