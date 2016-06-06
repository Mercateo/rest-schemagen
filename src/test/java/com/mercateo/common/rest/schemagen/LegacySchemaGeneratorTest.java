package com.mercateo.common.rest.schemagen;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.ws.rs.NotFoundException;

import com.mercateo.common.rest.schemagen.generator.ImmutableJsonPropertyResult;
import com.mercateo.common.rest.schemagen.generator.JsonPropertyResult;
import com.mercateo.common.rest.schemagen.generator.ObjectContextBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.Property;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.CharStreams;
import com.mercateo.common.rest.schemagen.generator.ObjectContext;
import com.mercateo.common.rest.schemagen.generictype.GenericType;
import com.mercateo.common.rest.schemagen.parameter.CallContext;
import com.mercateo.common.rest.schemagen.types.ObjectWithSchema;

public class LegacySchemaGeneratorTest {

    private JsonProperty rootJsonProperty;

    private Set<JsonProperty> referencedElements;

    private SchemaPropertyGenerator schemaGenerator;

    private CallContext callContext = CallContext.create();

    private SchemaPropertyContext schemaPropertyContext = new SchemaPropertyContext(callContext, (o,
            c) -> true);

    @Before
    public void setUp() throws Exception {
        schemaGenerator = new SchemaPropertyGenerator();
    }

    private void createSchemaFor(Class<?> clazz) {
        updateSchemaPropertyResult(schemaGenerator.generateSchemaProperty(createObjectContextBuilderFor(clazz),
                schemaPropertyContext));
    }

    private <T> ObjectContextBuilder<T> createObjectContextBuilderFor(Class<T> clazz) {
        return ObjectContext.buildFor(clazz);
    }


    private void createSchemaFor(GenericType<?> clazz) {
        updateSchemaPropertyResult(schemaGenerator.generateSchemaProperty(createObjectContextBuilderFor(clazz),
                schemaPropertyContext));
    }

    private <T> ObjectContextBuilder<T> createObjectContextBuilderFor(GenericType<T> genericType) {
        return ObjectContext.buildFor(genericType);
    }

    @SuppressWarnings("unchecked")
    private <T> void createAllowedValuesSchema(T object) {
        updateSchemaPropertyResult(schemaGenerator.generateSchemaProperty(createObjectContextBuilderFor((Class<T>) object
                .getClass()).addAllowedValues(object), schemaPropertyContext));
    }

    private void updateSchemaPropertyResult(JsonPropertyResult jsonPropertyResult) {
        rootJsonProperty = jsonPropertyResult.getRoot();
        referencedElements = jsonPropertyResult.getReferencedElements();
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
        JsonProperty bigDecimal = getByName("bigDecimal");
        assertThat(bigDecimal.getType()).isEqualTo(PropertyType.NUMBER);
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
        JsonProperty anEnum = getByName("anEnum");
        assertThat(anEnum.getType()).isEqualTo(PropertyType.STRING);
        List<String> allowedValues = anEnum.getAllowedValues();
        assertThat(allowedValues).isNotNull().hasSize(2).contains("VALUE_1", "VALUE_2");
    }

    @Test
    public void shouldUnrollGenerics() {
        createSchemaFor(TestRto.class);
        JsonProperty array = getByName("twoDimStringArray");
        assertThat(array.getType()).isEqualTo(PropertyType.ARRAY);
        JsonProperty nestedJsonProperty = array.getProperties().get(0);
        assertThat(nestedJsonProperty.getType()).isEqualTo(PropertyType.ARRAY);
        assertThat(nestedJsonProperty.getProperties().get(0).getType()).isEqualTo(PropertyType.STRING);
    }

    @Test
    public void shouldMapRecursiveDataStructures() {
        createSchemaFor(TestRto.class);
        assertThat(getByName("recursiveElement").getRef()).isEqualTo("#");
    }

    @Test
    public void shouldDelegateToAnnotatedGenerator() {
        createSchemaFor(TestRto.class);
        assertThat(getByName("hasOwnGenerator").getIndividualSchemaGenerator()).isEqualTo(
                TestSchemaGenerator.class);
    }

    @Test
    public void shouldMapRecursiveNonRootElements() {
        createSchemaFor(TestRto.class);
        JsonProperty element = getByName("nestedRecursiveElement");
        assertThat(element.getType()).isEqualTo(PropertyType.OBJECT);
        assertThat(element.getPath()).isEqualTo(getByName(element.getProperties(), "nextThing")
                .getRef());
        assertThat(referencedElements).contains(element);
    }

    @Test
    public void shouldProduceCorrectJson() throws IOException, JSONException {
        createSchemaFor(TestRto.class);
        final InputStream resourceAsStream = getClass()
                .getResourceAsStream("/SchemaGeneratorOutputTestFile.json");
        final InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream, "UTF-8");
        JSONObject target = new JSONObject(CharStreams.toString(inputStreamReader));
        JSONObject actual = new JSONObject(new PropertyJsonSchemaMapper().toJson(ImmutableJsonPropertyResult.of(rootJsonProperty, referencedElements))
                .toString());
        assertThat(actual.toString(2)).isEqualTo(target.toString(2));
    }

    @Test
    public void shouldMapHierarchicalUserRto() throws NoSuchMethodException {
        createSchemaFor(GenericType.of(getClass().getDeclaredMethod("unused")
                .getGenericReturnType()));
        assertThat(rootJsonProperty.getType()).isEqualTo(PropertyType.OBJECT);
        assertThat(rootJsonProperty.getProperties()).hasSize(12);
        assertThat(rootJsonProperty.getPropertyByName("string").getType()).isEqualTo(PropertyType.STRING);
        assertThat(rootJsonProperty.getPropertyByName("requiredString").getType()).isEqualTo(
                PropertyType.STRING);
        assertThat(rootJsonProperty.getPropertyByName("constrainedString").getType()).isEqualTo(
                PropertyType.STRING);
        assertThat(rootJsonProperty.getPropertyByName("integer").getType()).isEqualTo(PropertyType.INTEGER);
        assertThat(rootJsonProperty.getPropertyByName("bigDecimal").getType()).isEqualTo(PropertyType.NUMBER);
        assertThat(rootJsonProperty.getPropertyByName("bool").getType()).isEqualTo(PropertyType.BOOLEAN);
        assertThat(rootJsonProperty.getPropertyByName("boxedBool").getType()).isEqualTo(PropertyType.BOOLEAN);
        assertThat(rootJsonProperty.getPropertyByName("hasOwnGenerator").getType()).isEqualTo(
                PropertyType.OBJECT);
        assertThat(rootJsonProperty.getPropertyByName("anEnum").getType()).isEqualTo(PropertyType.STRING);
        assertThat(rootJsonProperty.getPropertyByName("twoDimStringArray").getType()).isEqualTo(
                PropertyType.ARRAY);
        assertThat(rootJsonProperty.getPropertyByName("recursiveElement").getType()).isEqualTo(
                PropertyType.OBJECT);
        assertThat(rootJsonProperty.getPropertyByName("nestedRecursiveElement").getType()).isEqualTo(
                PropertyType.OBJECT);
        JsonProperty firstArray = rootJsonProperty.getPropertyByName("twoDimStringArray");
        assertThat(firstArray.getType()).isEqualTo(PropertyType.ARRAY);
        assertThat(firstArray.getProperties()).hasSize(1);
        JsonProperty secondArray = firstArray.getProperties().get(0);
        assertThat(secondArray.getType()).isEqualTo(PropertyType.ARRAY);
        assertThat(secondArray.getProperties()).hasSize(1);
        JsonProperty arrayType = secondArray.getProperties().get(0);
        assertThat(arrayType.getType()).isEqualTo(PropertyType.STRING);
        assertThat(arrayType.getProperties()).isEmpty();
    }

    @Test
    public void shouldCreateAllowedValueEnum() {
        TestRto testRto = new TestRto();
        testRto.requiredString = "allowed_string";
        testRto.string = "this|is|an|enum";
        createAllowedValuesSchema(testRto);
        final List<String> prop = rootJsonProperty.getPropertyByName("requiredString").getAllowedValues();
        assertThat(prop).hasSize(1);
        assertThat(prop).contains("allowed_string");
        final List<String> otherProp = rootJsonProperty.getPropertyByName("string").getAllowedValues();
        assertThat(otherProp).hasSize(1);
        assertThat(otherProp).contains("this|is|an|enum");
    }

    @Test(expected = NoSuchElementException.class)
    public void getPropertyByNameShouldThrow() {
        createSchemaFor(TestRto.class);
        rootJsonProperty.getPropertyByName("nonExistentProperty");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNegativeSizeConstraint() {
        createSchemaFor(NegativeSizeConstraintRto.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnInconsistentConstraints() {
        createSchemaFor(InvalidTestRto.class);
    }

    @SuppressWarnings("unused")
    private ObjectWithSchema<TestRto> unused() {
        return null;
    }

    private JsonProperty getByName(String name) {
        return getByName(rootJsonProperty.getProperties(), name);
    }

    private JsonProperty getByName(List<JsonProperty> properties, String name) {
        for (JsonProperty p : properties) {
            if (p.getName().equals(name))
                return p;
        }
        throw new NotFoundException(String.format("No property with name %s", name));
    }

    @Test
    public void testPaginated() {
        createSchemaFor(TestPaginatedResponse.class);
        assertThat(rootJsonProperty.getType()).isEqualTo(PropertyType.OBJECT);
        assertThat(rootJsonProperty.getProperties()).hasSize(4);
        assertThat(rootJsonProperty.getPropertyByName("members").getType()).isEqualTo(PropertyType.ARRAY);
        assertThat(rootJsonProperty.getPropertyByName("total").getType()).isEqualTo(PropertyType.INTEGER);
        assertThat(rootJsonProperty.getPropertyByName("offset").getType()).isEqualTo(PropertyType.INTEGER);
        assertThat(rootJsonProperty.getPropertyByName("limit").getType()).isEqualTo(PropertyType.INTEGER);
    }

    @Test
    public void testSubclassing() {
        createSchemaFor(SubclassTestRto.class);
        assertThat(getByName("substring").getType()).isEqualTo(PropertyType.STRING);
        assertThat(getByName("string").getType()).isEqualTo(PropertyType.STRING);
    }
}