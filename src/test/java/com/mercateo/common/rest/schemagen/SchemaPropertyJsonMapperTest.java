package com.mercateo.common.rest.schemagen;


import java.lang.reflect.Type;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mercateo.common.rest.schemagen.generator.ObjectContext;
import com.mercateo.common.rest.schemagen.generictype.GenericType;
import com.mercateo.common.rest.schemagen.parameter.CallContext;

import static org.assertj.core.api.Assertions.assertThat;

public class SchemaPropertyJsonMapperTest {

    private SchemaPropertyGenerator schemaGenerator;

    private PropertyJsonSchemaMapper propertyJsonSchemaMapper;

    @Before
    public void setUp() {
        schemaGenerator = new SchemaPropertyGenerator();
        propertyJsonSchemaMapper = new PropertyJsonSchemaMapper();
    }

    @Test
    public void testClassHierarchiyWithSameType() {
        Property property = generateSchemaProperty(ExtendedResponse.class);

        final ObjectNode jsonNodes = propertyJsonSchemaMapper.toJson(property);

        assertThat(jsonNodes.size()).isEqualTo(2);
    }

    private Property generateSchemaProperty(Type type) {
        return generateSchemaProperty(ObjectContext.buildFor(GenericType.of(type)));
    }

    private Property generateSchemaProperty(ObjectContext.Builder<?> objectContextBuilder) {
        return generateSchemaProperty(objectContextBuilder, new SchemaPropertyContext(CallContext
                .create(), (r, o) -> true));
    }

    private Property generateSchemaProperty(ObjectContext.Builder<?> objectContextBuilder,
            SchemaPropertyContext context) {
        return schemaGenerator.generateSchemaProperty(objectContextBuilder, context);
    }

    public static class ValueResponse {
        public EmbeddedResponse embedded;

        public URL url;
    }

    public static class ExtendedResponse extends ValueResponse {
        URL subUrl;
    }

    public static class EmbeddedResponse {
        public URL embeddedUrl;
    }
}
