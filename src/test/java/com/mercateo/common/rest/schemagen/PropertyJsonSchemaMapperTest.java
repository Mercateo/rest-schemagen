package com.mercateo.common.rest.schemagen;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Type;
import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.mercateo.common.rest.schemagen.generator.JsonPropertyResult;
import com.mercateo.common.rest.schemagen.generator.ObjectContext;
import com.mercateo.common.rest.schemagen.generator.ObjectContextBuilder;
import com.mercateo.common.rest.schemagen.generictype.GenericType;
import com.mercateo.common.rest.schemagen.json.mapper.PropertyJsonSchemaMapper;
import com.mercateo.common.rest.schemagen.parameter.CallContext;

public class PropertyJsonSchemaMapperTest {

    private SchemaPropertyGenerator schemaGenerator;

    private PropertyJsonSchemaMapper propertyJsonSchemaMapper;

    @BeforeEach
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
    public void shouldMapFloatNumberValue() {
        final ValueResponse allowedValue = new ValueResponse();
        allowedValue.floatNumber1 = 10.5f;

        final ValueResponse defaultValue = new ValueResponse();
        defaultValue.floatNumber1 = 2.0f;

        final JsonPropertyResult jsonPropertyResult = generateSchemaProperty(ObjectContext.buildFor(ValueResponse.class).addAllowedValues(allowedValue).withDefaultValue(defaultValue));

        final ObjectNode rootNode = propertyJsonSchemaMapper.toJson(jsonPropertyResult);

        final JsonNode floatNumber = rootNode.get("properties").get("floatNumber1");

        assertThat(floatNumber.get("type").asText()).isEqualTo("number");

        final ImmutableList<JsonNode> allowedValues = ImmutableList.copyOf(floatNumber.get("enum").elements());
        assertThat(allowedValues).extracting(JsonNode::asDouble).containsExactly(10.5);

        final JsonNode defaultValueNode = floatNumber.get("default");
        assertThat(defaultValueNode.asDouble()).isEqualTo(2.0f);
    }


    @Test
    public void shouldMapDoubleNumberValue() {
        final ValueResponse allowedValue = new ValueResponse();
        allowedValue.doubleNumber1 = 10.5d;

        final ValueResponse defaultValue = new ValueResponse();
        defaultValue.doubleNumber1 = 2.0d;

        final JsonPropertyResult jsonPropertyResult = generateSchemaProperty(ObjectContext.buildFor(ValueResponse.class).addAllowedValues(allowedValue).withDefaultValue(defaultValue));

        final ObjectNode rootNode = propertyJsonSchemaMapper.toJson(jsonPropertyResult);

        final JsonNode doubleNumber = rootNode.get("properties").get("doubleNumber1");

        assertThat(doubleNumber.get("type").asText()).isEqualTo("number");

        final ImmutableList<JsonNode> allowedValues = ImmutableList.copyOf(doubleNumber.get("enum").elements());
        assertThat(allowedValues).extracting(JsonNode::asDouble).containsExactly(10.5);

        final JsonNode defaultValueNode = doubleNumber.get("default");
        assertThat(defaultValueNode.asDouble()).isEqualTo(2.0d);
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


    private static class ValueResponse {
        EmbeddedResponse embedded;

        URL url;

        String string1;

        Integer integer1;

        Float floatNumber1;

        Double doubleNumber1;

        Boolean boolean1;
    }

    private static class ExtendedResponse extends ValueResponse {
        URL subUrl;
    }

    private static class EmbeddedResponse {
        public URL embeddedUrl;
    }
}
