package com.mercateo.common.rest.schemagen.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.mercateo.common.rest.schemagen.JsonHyperSchema;

import org.junit.Test;

public class ObjectWithSchemaCreatorTest {
    @Test
    public void shouldWrapObjectAndSchema() throws Exception {
        Object object = new Object();
        JsonHyperSchema schema = mock(JsonHyperSchema.class);

        ObjectWithSchema<Object> objectWithSchema = new ObjectWithSchemaCreator().create(object, schema);

        assertThat(objectWithSchema.getObject()).isEqualTo(object);
        assertThat(objectWithSchema.getSchema()).isEqualTo(schema);
    }
}