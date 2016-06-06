package com.mercateo.common.rest.schemagen;


import java.lang.reflect.Type;
import java.net.URL;

import com.mercateo.common.rest.schemagen.generator.JsonPropertyResult;
import com.mercateo.common.rest.schemagen.generator.ObjectContextBuilder;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mercateo.common.rest.schemagen.generator.ObjectContext;
import com.mercateo.common.rest.schemagen.generictype.GenericType;
import com.mercateo.common.rest.schemagen.parameter.CallContext;

import static org.assertj.core.api.Assertions.assertThat;

public class SchemaJsonPropertyJsonMapperTest {

    private SchemaPropertyGenerator schemaGenerator;

    private PropertyJsonSchemaMapper propertyJsonSchemaMapper;

    @Before
    public void setUp() {
        schemaGenerator = new SchemaPropertyGenerator();
        propertyJsonSchemaMapper = new PropertyJsonSchemaMapper();
    }

    @Test
    public void testClassHierarchiyWithSameType() {
        JsonPropertyResult jsonProperty = generateSchemaProperty(ExtendedResponse.class);

        final ObjectNode jsonNodes = propertyJsonSchemaMapper.toJson(jsonProperty);

        assertThat(jsonNodes.size()).isEqualTo(2);
    }

    private JsonPropertyResult generateSchemaProperty(Type type) {
        return generateSchemaProperty(ObjectContext.buildFor(GenericType.of(type)));
    }

    private JsonPropertyResult generateSchemaProperty(ObjectContextBuilder<?> objectContextBuilder) {
        return generateSchemaProperty(objectContextBuilder, new SchemaPropertyContext(CallContext
                .create(), (r, o) -> true));
    }

    private JsonPropertyResult generateSchemaProperty(ObjectContextBuilder<?> objectContextBuilder,
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
