package com.mercateo.common.rest.schemagen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.StrictAssertions.assertThat;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.ws.rs.NotFoundException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.CharStreams;
import com.mercateo.common.rest.schemagen.generator.ObjectContext;
import com.mercateo.common.rest.schemagen.generictype.GenericType;
import com.mercateo.common.rest.schemagen.parameter.CallContext;
import com.mercateo.common.rest.schemagen.types.ObjectWithSchema;

public class LegacySchemaGenerator0Test {

    private Property schema;

    private SchemaPropertyGenerator schemaGenerator;

    private CallContext callContext = CallContext.create();

    private SchemaPropertyContext schemaPropertyContext = new SchemaPropertyContext(callContext, (o,
            c) -> true);

    @Before
    public void setUp() throws Exception {
        schemaGenerator = new SchemaPropertyGenerator();
    }

    private void createSchemaFor(Class<?> clazz) {
        schema = schemaGenerator.generateSchemaProperty(ObjectContext.buildFor(clazz),
                schemaPropertyContext);
    }

    private void createSchemaFor(GenericType<?> clazz) {
        schema = schemaGenerator.generateSchemaProperty(ObjectContext.buildFor(clazz),
                schemaPropertyContext);
    }

    @SuppressWarnings("unchecked")
    private <T> void createAllowedValuesSchema(T object) {
        schema = schemaGenerator.generateSchemaProperty(ObjectContext.buildFor((Class<T>) object
                .getClass()).withAllowedValue(object), schemaPropertyContext);
    }

    @Test
    public void shouldMapString() {
        createSchemaFor(TestRto.class);
        assertThat(getByName("string").getType()).isEqualTo(PropertyType.STRING);
    }

    @Test
    public void shouldMarkRequiredElement() {
        createSchemaFor(TestRto.class);
        assertThat(getByName("requiredString").isRequired()).isTrue();
    }

    @Test(expected = NotFoundException.class)
    public void shouldNotIncludeIgnoredElement() {
        createSchemaFor(TestRto.class);
        getByName("ignoredString");
    }

    @Test
    public void shouldSetSizeConstraints() {
        createSchemaFor(TestRto.class);
        assertThat(getByName("constrainedString").getSizeConstraints().getMax().get()).isEqualTo(8);
        assertThat(getByName("constrainedString").getSizeConstraints().getMin().get()).isEqualTo(5);
    }

    @Test
    public void shouldMapInteger() {
        createSchemaFor(TestRto.class);
        assertThat(getByName("integer").getType()).isEqualTo(PropertyType.INTEGER);
    }

    @Test
    public void shouldMapBigDecimal() {
        createSchemaFor(TestRto.class);
        Property bigDecimal = getByName("bigDecimal");
        assertThat(bigDecimal.getType()).isEqualTo(PropertyType.OBJECT);
        assertThat(getByName(bigDecimal.getProperties(), "scale").getType()).isEqualTo(
                PropertyType.INTEGER);
        assertThat(getByName(bigDecimal.getProperties(), "precision").getType()).isEqualTo(
                PropertyType.INTEGER);
    }

    @Test
    public void shouldMapBooleans() {
        createSchemaFor(TestRto.class);
        assertThat(getByName("bool").getType()).isEqualTo(PropertyType.BOOLEAN);
        assertThat(getByName("boxedBool").getType()).isEqualTo(PropertyType.BOOLEAN);
    }

    @Test
    public void shouldMapEnumAllowedValues() {
        createSchemaFor(TestRto.class);
        Property anEnum = getByName("anEnum");
        assertThat(anEnum.getType()).isEqualTo(PropertyType.STRING);
        List<String> allowedValues = anEnum.getAllowedValues();
        assertThat(allowedValues).isNotNull().hasSize(2).contains("VALUE_1", "VALUE_2");
    }

    @Test
    public void shouldUnrollGenerics() {
        createSchemaFor(TestRto.class);
        Property array = getByName("twoDimStringArray");
        assertThat(array.getType()).isEqualTo(PropertyType.ARRAY);
        Property nestedProperty = array.getProperties().get(0);
        assertThat(nestedProperty.getType()).isEqualTo(PropertyType.ARRAY);
        assertThat(nestedProperty.getProperties().get(0).getType()).isEqualTo(PropertyType.STRING);
    }

    @Test
    public void shouldMapRecursiveDataStructures() {
        createSchemaFor(TestRto.class);
        assertThat(getByName("recursiveElement").getRef()).isEqualTo("#");
    }

    @Test
    public void shouldDelegateToAnnotatedGenerator() {
        createSchemaFor(TestRto.class);
        assertThat(getByName("hasOwnGenerator").getGenerator()).isEqualTo(
                TestSchemaGenerator.class);
    }

    @Test
    public void shouldMapRecursiveNonRootElements() {
        createSchemaFor(TestRto.class);
        Property element = getByName("nestedRecursiveElement");
        assertThat(element.getType()).isEqualTo(PropertyType.OBJECT);
        assertThat(element.getId()).isEqualTo(getByName(element.getProperties(), "nextThing")
                .getRef());
    }

    @Test
    public void shouldProduceCorrectJson() throws IOException, JSONException {
        createSchemaFor(TestRto.class);
        JSONObject target = new JSONObject(CharStreams.toString(new InputStreamReader(getClass()
                .getResourceAsStream("SchemaGeneratorOutputTestFile.json"), "UTF-8")));
        JSONObject actual = new JSONObject(new PropertyJsonSchemaMapper().toJson(schema)
                .toString());
        assertThat(actual.toString(2)).isEqualTo(target.toString(2));
    }

    @Test
    public void shouldMapHierarchicalUserRto() throws NoSuchMethodException {
        createSchemaFor(GenericType.of(getClass().getDeclaredMethod("unused")
                .getGenericReturnType()));
        assertThat(schema.getType()).isEqualTo(PropertyType.OBJECT);
        assertThat(schema.getProperties()).hasSize(12);
        assertThat(schema.getPropertyByName("string").getType()).isEqualTo(PropertyType.STRING);
        assertThat(schema.getPropertyByName("requiredString").getType()).isEqualTo(
                PropertyType.STRING);
        assertThat(schema.getPropertyByName("constrainedString").getType()).isEqualTo(
                PropertyType.STRING);
        assertThat(schema.getPropertyByName("integer").getType()).isEqualTo(PropertyType.INTEGER);
        assertThat(schema.getPropertyByName("bigDecimal").getType()).isEqualTo(PropertyType.OBJECT);
        assertThat(schema.getPropertyByName("bool").getType()).isEqualTo(PropertyType.BOOLEAN);
        assertThat(schema.getPropertyByName("boxedBool").getType()).isEqualTo(PropertyType.BOOLEAN);
        assertThat(schema.getPropertyByName("hasOwnGenerator").getType()).isEqualTo(
                PropertyType.OBJECT);
        assertThat(schema.getPropertyByName("anEnum").getType()).isEqualTo(PropertyType.STRING);
        assertThat(schema.getPropertyByName("twoDimStringArray").getType()).isEqualTo(
                PropertyType.ARRAY);
        assertThat(schema.getPropertyByName("recursiveElement").getType()).isEqualTo(
                PropertyType.OBJECT);
        assertThat(schema.getPropertyByName("nestedRecursiveElement").getType()).isEqualTo(
                PropertyType.OBJECT);
        Property firstArray = schema.getPropertyByName("twoDimStringArray");
        assertThat(firstArray.getType()).isEqualTo(PropertyType.ARRAY);
        assertThat(firstArray.getProperties()).hasSize(1);
        Property secondArray = firstArray.getProperties().get(0);
        assertThat(secondArray.getType()).isEqualTo(PropertyType.ARRAY);
        assertThat(secondArray.getProperties()).hasSize(1);
        Property arrayType = secondArray.getProperties().get(0);
        assertThat(arrayType.getType()).isEqualTo(PropertyType.STRING);
        assertThat(arrayType.getProperties()).isEmpty();
    }

    @Test
    public void shouldCreateAllowedValueEnum() {
        TestRto testRto = new TestRto();
        testRto.requiredString = "allowed_string";
        testRto.string = "this|is|an|enum";
        createAllowedValuesSchema(testRto);
        final List<String> prop = schema.getPropertyByName("requiredString").getAllowedValues();
        assertThat(prop).hasSize(1);
        assertThat(prop).contains("allowed_string");
        final List<String> otherProp = schema.getPropertyByName("string").getAllowedValues();
        assertThat(otherProp).hasSize(1);
        assertThat(otherProp).contains("this|is|an|enum");
    }

    @SuppressWarnings("unused")
    private ObjectWithSchema<TestRto> unused() {
        return null;
    }

    private Property getByName(String name) {
        return getByName(schema.getProperties(), name);
    }

    private Property getByName(List<Property> properties, String name) {
        for (Property p : properties) {
            if (p.getName().equals(name))
                return p;
        }
        throw new NotFoundException(String.format("No property with name %s", name));
    }

    @Test
    public void testPaginated() {
        createSchemaFor(TestPaginatedResponse.class);
        assertThat(schema.getType()).isEqualTo(PropertyType.OBJECT);
        assertThat(schema.getProperties()).hasSize(4);
        assertThat(schema.getPropertyByName("members").getType()).isEqualTo(PropertyType.ARRAY);
        assertThat(schema.getPropertyByName("total").getType()).isEqualTo(PropertyType.INTEGER);
        assertThat(schema.getPropertyByName("offset").getType()).isEqualTo(PropertyType.INTEGER);
        assertThat(schema.getPropertyByName("limit").getType()).isEqualTo(PropertyType.INTEGER);
    }

    @Test
    public void testSubclassing() {
        createSchemaFor(SubclassTestRto.class);
        assertThat(getByName("substring").getType()).isEqualTo(PropertyType.STRING);
        assertThat(getByName("string").getType()).isEqualTo(PropertyType.STRING);
    }
}