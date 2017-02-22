package com.mercateo.common.rest.schemagen;


import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
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

    @Test
    public void shouldMapStringValue() {
        final ValueResponse allowedValue = new ValueResponse();
        allowedValue.string1 = "allowed";

        final ValueResponse defaultValue = new ValueResponse();
        defaultValue.string1 = "default";

        final JsonPropertyResult jsonPropertyResult = generateSchemaProperty(ObjectContext.buildFor(ValueResponse.class).addAllowedValues(allowedValue).withDefaultValue(defaultValue));

        final ObjectNode rootNode = propertyJsonSchemaMapper.toJson(jsonPropertyResult);

        final JsonNode stringNode = rootNode.get("properties").get("string1");

        assertThat(stringNode.get("type").asText()).isEqualTo("string");

        final ImmutableList<JsonNode> allowedValues = ImmutableList.copyOf(stringNode.get("enum").elements());
        assertThat(allowedValues).extracting(JsonNode::asText).containsExactly("allowed");

        final JsonNode defaultValueNode = stringNode.get("default");
        assertThat(defaultValueNode.asText()).isEqualTo("default");
    }

    @Test
    public void shouldMapBooleanValue() {
        final ValueResponse allowedValue = new ValueResponse();
        allowedValue.boolean1 = true;

        final ValueResponse defaultValue = new ValueResponse();
        defaultValue.boolean1 = false;

        final JsonPropertyResult jsonPropertyResult = generateSchemaProperty(ObjectContext.buildFor(ValueResponse.class).addAllowedValues(allowedValue).withDefaultValue(defaultValue));

        final ObjectNode rootNode = propertyJsonSchemaMapper.toJson(jsonPropertyResult);

        final JsonNode stringNode = rootNode.get("properties").get("boolean1");

        assertThat(stringNode.get("type").asText()).isEqualTo("boolean");

        final ImmutableList<JsonNode> allowedValues = ImmutableList.copyOf(stringNode.get("enum").elements());
        assertThat(allowedValues).extracting(JsonNode::asBoolean).containsExactly(true);

        final JsonNode defaultValueNode = stringNode.get("default");
        assertThat(defaultValueNode.asBoolean()).isEqualTo(false);
    }

    @Test
    public void shouldMapIntegerValue() {
        final ValueResponse allowedValue = new ValueResponse();
        allowedValue.integer1 = 10;

        final ValueResponse defaultValue = new ValueResponse();
        defaultValue.integer1 = 1;

        final JsonPropertyResult jsonPropertyResult = generateSchemaProperty(ObjectContext.buildFor(ValueResponse.class).addAllowedValues(allowedValue).withDefaultValue(defaultValue));

        final ObjectNode rootNode = propertyJsonSchemaMapper.toJson(jsonPropertyResult);

        final JsonNode integer = rootNode.get("properties").get("integer1");

        assertThat(integer.get("type").asText()).isEqualTo("integer");

        final ImmutableList<JsonNode> allowedValues = ImmutableList.copyOf(integer.get("enum").elements());
        assertThat(allowedValues).extracting(JsonNode::asInt).containsExactly(10);

        final JsonNode defaultValueNode = integer.get("default");
        assertThat(defaultValueNode.asInt()).isEqualTo(1);
    }

    @Test
    public void shouldMapNumberValue() {
        final ValueResponse allowedValue = new ValueResponse();
        allowedValue.number1 = 10.5f;

        final ValueResponse defaultValue = new ValueResponse();
        defaultValue.number1 = 2.0f;

        final JsonPropertyResult jsonPropertyResult = generateSchemaProperty(ObjectContext.buildFor(ValueResponse.class).addAllowedValues(allowedValue).withDefaultValue(defaultValue));

        final ObjectNode rootNode = propertyJsonSchemaMapper.toJson(jsonPropertyResult);

        final JsonNode integer = rootNode.get("properties").get("number1");

        assertThat(integer.get("type").asText()).isEqualTo("number");

        final ImmutableList<JsonNode> allowedValues = ImmutableList.copyOf(integer.get("enum").elements());
        assertThat(allowedValues).extracting(JsonNode::asDouble).containsExactly(10.5);

        final JsonNode defaultValueNode = integer.get("default");
        assertThat(defaultValueNode.asDouble()).isEqualTo(2.0f);
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

        public String string1;

        public Integer integer1;

        public Float number1;

        public Boolean boolean1;
    }

    public static class ExtendedResponse extends ValueResponse {
        URL subUrl;
    }

    public static class EmbeddedResponse {
        public URL embeddedUrl;
    }
}
