package com.mercateo.common.rest.schemagen;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.ImmutableList;
import com.mercateo.common.rest.schemagen.generator.JsonPropertyResult;
import com.mercateo.common.rest.schemagen.generator.ObjectContext;
import com.mercateo.common.rest.schemagen.generator.ObjectContextBuilder;
import com.mercateo.common.rest.schemagen.generictype.GenericType;
import com.mercateo.common.rest.schemagen.parameter.CallContext;
import com.mercateo.common.rest.schemagen.types.ListResponse;
import com.mercateo.common.rest.schemagen.types.ObjectWithSchema;
import com.mercateo.common.rest.schemagen.types.WrappedList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.PathParam;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(MockitoJUnitRunner.class)
public class SchemaJsonPropertyGeneratorTest {

    private SchemaPropertyGenerator schemaGenerator;

    @Before
    public void setUp() {
        schemaGenerator = new SchemaPropertyGenerator();
    }

    @Test
    public void testString() {
        JsonProperty jsonProperty = generateSchemaProperty(String.class);
        assertThat(jsonProperty.getName()).isEqualTo("String");
        assertThat(jsonProperty.getType()).isEqualTo(PropertyType.STRING);
        assertThat(jsonProperty.getProperties()).isEmpty();
    }

    @Test
    public void testObject() {
        JsonProperty jsonProperty = generateSchemaProperty(SchemaObject.class);
        assertThat(jsonProperty.getName()).isEqualTo("SchemaObject");
        assertThat(jsonProperty.getType()).isEqualTo(PropertyType.OBJECT);
        List<JsonProperty> properties = jsonProperty.getProperties();
        assertThat(properties).hasSize(2);

        assertThat(properties.get(0).getName()).isEqualTo("count");
        assertThat(properties.get(0).getType()).isEqualTo(PropertyType.INTEGER);
        assertThat(properties.get(0).getProperties()).isEmpty();

        assertThat(properties.get(1).getName()).isEqualTo("name");
        assertThat(properties.get(1).getType()).isEqualTo(PropertyType.STRING);
        assertThat(properties.get(1).getProperties()).isEmpty();
        // the third property is a path param!
        assertThat(properties.size()).isEqualTo(2);
    }

    @Test
    public void testObjectDefaultValues() {
        SchemaObject defaultValue = new SchemaObject();
        defaultValue.name = "<default>";

        final JsonProperty jsonProperty = generateSchemaProperty(ObjectContext.buildFor(SchemaObject.class)
                .withDefaultValue(defaultValue));

        List<JsonProperty> properties = jsonProperty.getProperties();
        assertThat(properties).hasSize(2);

        assertThat(properties.get(0).getName()).isEqualTo("count");
        assertThat(properties.get(0).getDefaultValue()).isNull();

        assertThat(properties.get(1).getName()).isEqualTo("name");
        assertThat(properties.get(1).getDefaultValue()).isEqualTo("<default>");
    }

    @Test
    public void testObjectAllowedValues() {
        SchemaObject allowedValues = new SchemaObject();
        allowedValues.name = "foo";

        final JsonProperty jsonProperty = generateSchemaProperty(ObjectContext.buildFor(SchemaObject.class)
                .addAllowedValues(allowedValues));

        List<JsonProperty> properties = jsonProperty.getProperties();
        assertThat(properties).hasSize(2);

        assertThat(properties.get(0).getName()).isEqualTo("count");
        assertThat(properties.get(0).getAllowedValues()).isEmpty();
        assertThat(properties.get(0).getDefaultValue()).isNull();

        assertThat(properties.get(1).getName()).isEqualTo("name");
        assertThat(properties.get(1).getAllowedValues()).containsExactly("foo");
        assertThat(properties.get(1).getDefaultValue()).isNull();
    }

    @Test
    public void testObjectCurrentValuesAndPathParams() {
        SchemaObject currentValue = new SchemaObject();

        final JsonProperty jsonProperty = generateSchemaProperty(ObjectContext.buildFor(SchemaObject.class)
                .withCurrentValue(currentValue));

        List<JsonProperty> properties = jsonProperty.getProperties();
        assertThat(properties).hasSize(3);
        assertThat(properties.get(2).getName()).isEqualTo("pathParameter");
    }

    @Test
    public void testObjectCurrentValuesAndPathParamsSet() {
        SchemaObject currentValue = new SchemaObject();
        currentValue.pathParameter = "path";

        final JsonProperty jsonProperty = generateSchemaProperty(ObjectContext.buildFor(SchemaObject.class)
                .withCurrentValue(currentValue));

        List<JsonProperty> properties = jsonProperty.getProperties();
        assertThat(properties).hasSize(2);

    }

    @Test
    public void testObjectAllowedAndDefaultValues() {
        SchemaObject allowedValues = new SchemaObject();
        allowedValues.name = "foo|bar|baz";

        SchemaObject defaultValue = new SchemaObject();
        defaultValue.name = "baz";

        final JsonProperty jsonProperty = generateSchemaProperty(ObjectContext.buildFor(SchemaObject.class)
                .addAllowedValues(allowedValues).withDefaultValue(defaultValue));

        List<JsonProperty> properties = jsonProperty.getProperties();
        assertThat(properties).hasSize(2);

        assertThat(properties.get(0).getName()).isEqualTo("count");
        assertThat(properties.get(0).getAllowedValues()).isEmpty();
        assertThat(properties.get(0).getDefaultValue()).isNull();

        assertThat(properties.get(1).getName()).isEqualTo("name");
        assertThat(properties.get(1).getAllowedValues()).containsOnly("foo|bar|baz");
        assertThat(properties.get(1).getDefaultValue()).isEqualTo("baz");
    }

    @Test
    public void testMultipleAllowedValues() {
        SchemaObject allowedValue1 = new SchemaObject();
        allowedValue1.name = "foo";
        SchemaObject allowedValue2 = new SchemaObject();
        allowedValue2.name = "bar";
        SchemaObject allowedValue3 = new SchemaObject();
        allowedValue3.name = "baz";
        List<SchemaObject> allowedValues = ImmutableList.of(allowedValue1, allowedValue2,
                allowedValue3);

        final JsonProperty jsonProperty = generateSchemaProperty(ObjectContext.buildFor(SchemaObject.class)
                .withAllowedValues(allowedValues));

        List<JsonProperty> properties = jsonProperty.getProperties();
        assertThat(properties).hasSize(2);

        assertThat(properties.get(0).getName()).isEqualTo("count");
        assertThat(properties.get(0).getAllowedValues()).isEmpty();
        assertThat(properties.get(0).getDefaultValue()).isNull();

        assertThat(properties.get(1).getName()).isEqualTo("name");
        assertThat(properties.get(1).getAllowedValues()).containsOnly("foo", "bar", "baz");
    }

    @SuppressWarnings("boxing")
    @Test
    public void testNestedObjectDefaultValues() {
        SchemaMasterObject defaultValue = new SchemaMasterObject();
        SchemaObject defaultObject = new SchemaObject();
        defaultObject.count = 5;
        defaultValue.schemaObject = defaultObject;

        final JsonProperty jsonProperty = generateSchemaProperty(ObjectContext.buildFor(
                SchemaMasterObject.class).withDefaultValue(defaultValue));

        List<JsonProperty> properties = jsonProperty.getProperties();
        assertThat(properties).hasSize(1);

        List<JsonProperty> nestedProperties = jsonProperty.getProperties();
        assertThat(nestedProperties).hasSize(1);

        List<JsonProperty> nestedObjectProperties = nestedProperties.get(0).getProperties();
        assertThat(nestedObjectProperties).hasSize(2);

        assertThat(nestedObjectProperties.get(0).getName()).isEqualTo("count");
        assertThat(nestedObjectProperties.get(0).getDefaultValue()).isEqualTo(5);

        assertThat(nestedObjectProperties.get(1).getName()).isEqualTo("name");
        assertThat(nestedObjectProperties.get(1).getDefaultValue()).isNull();
    }

    @Test
    public void testObjectWithEnumDefaultAllowedValues() {
        JsonProperty jsonProperty = generateSchemaProperty(SchemaObjectWithEnum.class);
        List<JsonProperty> properties = jsonProperty.getProperties();
        assertThat(properties).hasSize(1);

        assertThat(properties.get(0).getName()).isEqualTo("mode");
        assertThat(properties.get(0).getType()).isEqualTo(PropertyType.STRING);
        assertThat(properties.get(0).getAllowedValues()).containsExactly("FOO", "BAR");
        assertThat(properties.get(0).getDefaultValue()).isNull();
        assertThat(properties.get(0).getProperties()).isEmpty();
    }

    @Test
    public void testObjectWithEnumAllowedValues() {
        SchemaObjectWithEnum allowedValue = new SchemaObjectWithEnum();
        allowedValue.mode = TestEnum.BAR;

        JsonProperty jsonProperty = generateSchemaProperty(ObjectContext.buildFor(
                SchemaObjectWithEnum.class).addAllowedValues(allowedValue));
        List<JsonProperty> properties = jsonProperty.getProperties();
        assertThat(properties).hasSize(1);

        assertThat(properties.get(0).getName()).isEqualTo("mode");
        assertThat(properties.get(0).getType()).isEqualTo(PropertyType.STRING);
        assertThat(properties.get(0).getAllowedValues()).containsExactly("BAR");
        assertThat(properties.get(0).getDefaultValue()).isNull();
        assertThat(properties.get(0).getProperties()).isEmpty();
    }

    @Test
    public void testObjectWithEnumDefaultValues() {
        SchemaObjectWithEnum defaultValue = new SchemaObjectWithEnum();
        defaultValue.mode = TestEnum.BAR;

        JsonProperty jsonProperty = generateSchemaProperty(ObjectContext.buildFor(
                SchemaObjectWithEnum.class).withDefaultValue(defaultValue));
        List<JsonProperty> properties = jsonProperty.getProperties();
        assertThat(properties).hasSize(1);

        assertThat(properties.get(0).getName()).isEqualTo("mode");
        assertThat(properties.get(0).getType()).isEqualTo(PropertyType.STRING);
        assertThat(properties.get(0).getAllowedValues()).containsExactly("FOO", "BAR");
        assertThat(properties.get(0).getDefaultValue()).isEqualTo("BAR");
        assertThat(properties.get(0).getProperties()).isEmpty();
    }

    @Test
    public void testObjectWithArray() {
        JsonProperty jsonProperty = generateSchemaProperty(SchemaObjectWithArray.class);
        assertThat(jsonProperty.getName()).isEqualTo("SchemaObjectWithArray");
        assertThat(jsonProperty.getType()).isEqualTo(PropertyType.OBJECT);
        List<JsonProperty> properties = jsonProperty.getProperties();
        assertThat(properties).hasSize(1);

        assertThat(properties.get(0).getName()).isEqualTo("names");
        assertThat(properties.get(0).getType()).isEqualTo(PropertyType.ARRAY);
        List<JsonProperty> arrayProperties = properties.get(0).getProperties();
        assertThat(arrayProperties).hasSize(1);

        assertThat(arrayProperties.get(0).getName()).isEqualTo("");
        assertThat(arrayProperties.get(0).getType()).isEqualTo(PropertyType.STRING);
        assertThat(arrayProperties.get(0).getProperties()).isEmpty();
    }

    @Test
    public void testObjectWithGenericArray() {
        JsonProperty jsonProperty = generateSchemaProperty(SchemaObjectWithGenericArray.class);
        assertThat(jsonProperty.getName()).isEqualTo("SchemaObjectWithGenericArray");
        assertThat(jsonProperty.getType()).isEqualTo(PropertyType.OBJECT);
        List<JsonProperty> properties = jsonProperty.getProperties();
        assertThat(properties).hasSize(1);

        assertThat(properties.get(0).getName()).isEqualTo("names");
        assertThat(properties.get(0).getType()).isEqualTo(PropertyType.ARRAY);
        List<JsonProperty> arrayProperties = properties.get(0).getProperties();
        assertThat(arrayProperties).hasSize(1);

        assertThat(arrayProperties.get(0).getName()).isEqualTo("");
        assertThat(arrayProperties.get(0).getType()).isEqualTo(PropertyType.ARRAY);
        List<JsonProperty> listProperties = arrayProperties.get(0).getProperties();
        assertThat(listProperties).hasSize(1);

        assertThat(listProperties.get(0).getName()).isEqualTo("");
        assertThat(listProperties.get(0).getType()).isEqualTo(PropertyType.STRING);
        assertThat(listProperties.get(0).getProperties()).isEmpty();
    }

    @Test
    public void testObjectWithGenericWithArray() {
        JsonProperty jsonProperty = generateSchemaProperty(SchemaObjectWithGenericWithArray.class);
        assertThat(jsonProperty.getName()).isEqualTo("SchemaObjectWithGenericWithArray");
        assertThat(jsonProperty.getType()).isEqualTo(PropertyType.OBJECT);
        List<JsonProperty> properties = jsonProperty.getProperties();
        assertThat(properties).hasSize(1);

        assertThat(properties.get(0).getName()).isEqualTo("names");
        assertThat(properties.get(0).getType()).isEqualTo(PropertyType.ARRAY);
        List<JsonProperty> arrayProperties = properties.get(0).getProperties();
        assertThat(arrayProperties).hasSize(1);

        assertThat(arrayProperties.get(0).getName()).isEqualTo("");
        assertThat(arrayProperties.get(0).getType()).isEqualTo(PropertyType.ARRAY);
        List<JsonProperty> listProperties = arrayProperties.get(0).getProperties();
        assertThat(listProperties).hasSize(1);

        assertThat(listProperties.get(0).getName()).isEqualTo("");
        assertThat(listProperties.get(0).getType()).isEqualTo(PropertyType.STRING);
        assertThat(listProperties.get(0).getProperties()).isEmpty();
    }

    @Test
    public void testObjectWithGenericElement() {
        JsonProperty jsonProperty = generateSchemaProperty(SchemaObjectWithGenericElement.class);
        assertThat(jsonProperty.getName()).isEqualTo("SchemaObjectWithGenericElement");
        assertThat(jsonProperty.getType()).isEqualTo(PropertyType.OBJECT);
        List<JsonProperty> properties = jsonProperty.getProperties();
        assertThat(properties).hasSize(1);

        assertThat(properties.get(0).getName()).isEqualTo("values");
        assertThat(properties.get(0).getType()).isEqualTo(PropertyType.ARRAY);
        List<JsonProperty> arrayProperties = properties.get(0).getProperties();
        assertThat(arrayProperties).hasSize(1);

        assertThat(arrayProperties.get(0).getName()).isEqualTo("");
        assertThat(arrayProperties.get(0).getType()).isEqualTo(PropertyType.NUMBER);
        assertThat(arrayProperties.get(0).getProperties()).isEmpty();
    }

    @Test
    public void testObjectWithNestedArrayElement() {
        JsonProperty jsonProperty = generateSchemaProperty(SchemaObjectWithNestedArrayElement.class);
        assertThat(jsonProperty.getName()).isEqualTo("SchemaObjectWithNestedArrayElement");
        assertThat(jsonProperty.getType()).isEqualTo(PropertyType.OBJECT);
        List<JsonProperty> properties = jsonProperty.getProperties();
        assertThat(properties).hasSize(1);

        assertThat(properties.get(0).getName()).isEqualTo("values");
        assertThat(properties.get(0).getType()).isEqualTo(PropertyType.ARRAY);
        List<JsonProperty> outerArrayProperties = properties.get(0).getProperties();
        assertThat(outerArrayProperties).hasSize(1);

        assertThat(outerArrayProperties.get(0).getName()).isEqualTo("");
        assertThat(outerArrayProperties.get(0).getType()).isEqualTo(PropertyType.ARRAY);
        List<JsonProperty> innerArrayProperties = outerArrayProperties.get(0).getProperties();
        assertThat(innerArrayProperties).hasSize(1);

        assertThat(innerArrayProperties.get(0).getName()).isEqualTo("");
        assertThat(innerArrayProperties.get(0).getType()).isEqualTo(PropertyType.INTEGER);
        assertThat(innerArrayProperties.get(0).getProperties()).isEmpty();
    }

    @Test
    public void testObjectWithMapAsObject() {
        final JsonProperty jsonProperty = generateSchemaProperty(SchemaObjectWithMapAsObject.class);
        assertThat(jsonProperty.getType()).isEqualTo(PropertyType.OBJECT);
        List<JsonProperty> properties = jsonProperty.getProperties();
        assertThat(properties).hasSize(1);
    }

    @Test
    public void testObjectWithMapAsDict() {
        final JsonProperty jsonProperty = generateSchemaProperty(SchemaObjectWithMapAsDict.class);
        assertThat(jsonProperty.getType()).isEqualTo(PropertyType.OBJECT);
        List<JsonProperty> properties = jsonProperty.getProperties();
        assertThat(properties).hasSize(1);

        final List<JsonProperty> dictProperties = properties.get(0).getProperties();

        assertThat(dictProperties.get(0).getName()).isEqualTo("FOO");
        assertThat(dictProperties.get(0).getType()).isEqualTo(PropertyType.ARRAY);
        List<JsonProperty> innerArrayProperties = dictProperties.get(0).getProperties();
        assertThat(innerArrayProperties).hasSize(1);

        assertThat(dictProperties.get(1).getName()).isEqualTo("BAR");
        assertThat(dictProperties.get(1).getType()).isEqualTo(PropertyType.ARRAY);
        innerArrayProperties = dictProperties.get(1).getProperties();
        assertThat(innerArrayProperties).hasSize(1);
    }

    @Test
    public void testObjectWithMapAsDictWithEnumJsonValue() {
        final JsonProperty jsonProperty = generateSchemaProperty(SchemaObjectWithMapAsDictEnumJsonValue.class);
        assertThat(jsonProperty.getType()).isEqualTo(PropertyType.OBJECT);
        List<JsonProperty> properties = jsonProperty.getProperties();
        assertThat(properties).hasSize(1);

        final List<JsonProperty> dictProperties = properties.get(0).getProperties();

        assertThat(dictProperties.get(0).getName()).isEqualTo("fooOne");
        assertThat(dictProperties.get(0).getType()).isEqualTo(PropertyType.ARRAY);
        List<JsonProperty> innerArrayProperties = dictProperties.get(0).getProperties();
        assertThat(innerArrayProperties).hasSize(1);

        assertThat(dictProperties.get(1).getName()).isEqualTo("barTwo");
        assertThat(dictProperties.get(1).getType()).isEqualTo(PropertyType.ARRAY);
        innerArrayProperties = dictProperties.get(1).getProperties();
        assertThat(innerArrayProperties).hasSize(1);
    }

    @Test
    public void testObjectWithUnwrappedContent() {
        JsonProperty jsonProperty = generateSchemaProperty(SchemaObjectWithUnwrappedContent.class);
        assertThat(jsonProperty.getName()).isEqualTo("SchemaObjectWithUnwrappedContent");
        assertThat(jsonProperty.getType()).isEqualTo(PropertyType.OBJECT);
        List<JsonProperty> properties = jsonProperty.getProperties();
        assertThat(properties).hasSize(2);

        assertThat(properties.get(0).getName()).isEqualTo("count");
        assertThat(properties.get(0).getType()).isEqualTo(PropertyType.INTEGER);
        assertThat(properties.get(0).getProperties()).isEmpty();

        assertThat(properties.get(1).getName()).isEqualTo("name");
        assertThat(properties.get(1).getType()).isEqualTo(PropertyType.STRING);
        assertThat(properties.get(1).getProperties()).isEmpty();
    }

    @Test
    public void testObjectWithGenericTypeComponent() {
        JsonProperty jsonProperty = generateSchemaProperty(SchemaObjectWithIgnoredTypeComponent.class);
        assertThat(jsonProperty.getName()).isEqualTo("SchemaObjectWithIgnoredTypeComponent");
        assertThat(jsonProperty.getType()).isEqualTo(PropertyType.OBJECT);
        List<JsonProperty> properties = jsonProperty.getProperties();
        assertThat(properties).hasSize(1);

        assertThat(properties.get(0).getName()).isEqualTo("optional");
        assertThat(properties.get(0).getType()).isEqualTo(PropertyType.OBJECT);
        List<JsonProperty> optionalProperties = properties.get(0).getProperties();
        assertThat(optionalProperties).hasSize(1);

        assertThat(optionalProperties.get(0).getName()).isEqualTo("value");
        assertThat(optionalProperties.get(0).getType()).isEqualTo(PropertyType.STRING);
        assertThat(optionalProperties.get(0).getProperties()).isEmpty();
    }

    @Test
    public void testInheritedObject() {
        JsonProperty jsonProperty = generateSchemaProperty(InheritedSchemaObject.class);
        assertThat(jsonProperty.getName()).isEqualTo("InheritedSchemaObject");
        assertThat(jsonProperty.getType()).isEqualTo(PropertyType.OBJECT);
        List<JsonProperty> properties = jsonProperty.getProperties();
        assertThat(properties).hasSize(3);

        assertThat(properties.get(0).getName()).isEqualTo("count");
        assertThat(properties.get(0).getType()).isEqualTo(PropertyType.INTEGER);
        assertThat(properties.get(0).getProperties()).isEmpty();

        assertThat(properties.get(1).getName()).isEqualTo("enabled");
        assertThat(properties.get(1).getType()).isEqualTo(PropertyType.BOOLEAN);
        assertThat(properties.get(1).getProperties()).isEmpty();

        assertThat(properties.get(2).getName()).isEqualTo("name");
        assertThat(properties.get(2).getType()).isEqualTo(PropertyType.STRING);
        assertThat(properties.get(2).getProperties()).isEmpty();
    }

    @Test
    public void testInheritedGenericObjectWithWrappedPrimitiveShouldThrow() {
        assertThatThrownBy(() -> generateSchemaProperty(MessageResponse.class)) //
                .isExactlyInstanceOf(IllegalStateException.class) //
                .hasMessage("can not unwrap primitive type STRING");
    }

    @Test
    public void testInheritedGenericObjectWithWrappedObject() {
        JsonProperty jsonProperty = generateSchemaProperty(WrappedObject.class);
        assertThat(jsonProperty.getName()).isEqualTo("WrappedObject");
        assertThat(jsonProperty.getType()).isEqualTo(PropertyType.OBJECT);
        List<JsonProperty> properties = jsonProperty.getProperties();
        assertThat(properties).hasSize(2);

        assertThat(properties.get(0).getName()).isEqualTo("count");
        assertThat(properties.get(0).getType()).isEqualTo(PropertyType.INTEGER);
        assertThat(properties.get(0).getProperties()).isEmpty();

        assertThat(properties.get(1).getName()).isEqualTo("name");
        assertThat(properties.get(1).getType()).isEqualTo(PropertyType.STRING);
        assertThat(properties.get(1).getProperties()).isEmpty();
    }

    @Test
    public void testRecursiveObject() {
        JsonProperty jsonProperty = generateSchemaProperty(RecursiveSchemaObject.class);
        assertThat(jsonProperty.getName()).isEqualTo("RecursiveSchemaObject");
        assertThat(jsonProperty.getType()).isEqualTo(PropertyType.OBJECT);
        assertThat(jsonProperty.getPath()).isEqualTo("#");
        assertThat(jsonProperty.getRef()).isNull();
        List<JsonProperty> properties = jsonProperty.getProperties();
        assertThat(properties).extracting(JsonProperty::getName).containsExactly("children", "name");
        assertThat(properties).extracting(JsonProperty::getPath).containsExactly("#/children", "#/name");
        assertThat(properties).extracting(JsonProperty::getRef).containsExactly(null, null);

        final List<JsonProperty> childProperties = getPropertiesOfNamedElement(properties, "children");

        assertThat(childProperties).extracting(JsonProperty::getName).containsExactly("");
        assertThat(childProperties).extracting(JsonProperty::getPath).containsExactly("#/children/");
        assertThat(childProperties).extracting(JsonProperty::getRef).containsExactly("#");
    }

    @Test
    public void testObjectWithReferences() {
        JsonProperty jsonProperty = generateSchemaProperty(ObjectWithInternalReferences.class);
        assertThat(jsonProperty.getName()).isEqualTo("ObjectWithInternalReferences");
        assertThat(jsonProperty.getType()).isEqualTo(PropertyType.OBJECT);
        List<JsonProperty> properties = jsonProperty.getProperties();
        assertThat(properties).extracting(JsonProperty::getName).containsExactly("object1", "object2");
        assertThat(properties).extracting(JsonProperty::getPath).containsExactly("#/object1", "#/object2");
        assertThat(properties).extracting(JsonProperty::getRef).containsExactly(null, null);

        final List<JsonProperty> childProperties = getPropertiesOfNamedElement(properties, "object2");

        assertThat(childProperties).extracting(JsonProperty::getName).containsExactly("");
        assertThat(childProperties).extracting(JsonProperty::getPath).containsExactly("#/object2/");
        assertThat(childProperties).extracting(JsonProperty::getRef).containsNull();

        final List<JsonProperty> childChildProperties = getPropertiesOfNamedElement(childProperties, "");

        assertThat(childChildProperties).extracting(JsonProperty::getName).containsExactly("");
        assertThat(childChildProperties).extracting(JsonProperty::getPath).containsExactly("#/object2//");
        assertThat(childChildProperties).extracting(JsonProperty::getRef).containsExactly("#/object1/");
    }

    private List<JsonProperty> getPropertiesOfNamedElement(List<JsonProperty> properties, String propertyName) {
        return properties.stream()
                  .filter(p -> propertyName.equals(p.getName()))
                  .findFirst()
                  .map(JsonProperty::getProperties)
                  .orElse(Collections.emptyList());
    }

    @Test
    public void testInheritedGenericObject() {
        JsonProperty jsonProperty = generateSchemaProperty(ListObject.class);
        assertThat(jsonProperty.getName()).isEqualTo("ListObject");
        assertThat(jsonProperty.getType()).isEqualTo(PropertyType.OBJECT);
        List<JsonProperty> properties = jsonProperty.getProperties();
        assertThat(properties).hasSize(1);

        assertThat(properties.get(0).getName()).isEqualTo("members");
        assertThat(properties.get(0).getType()).isEqualTo(PropertyType.ARRAY);
        List<JsonProperty> array = properties.get(0).getProperties();
        assertThat(array).hasSize(1);

        assertThat(array.get(0).getName()).isEqualTo("");
        assertThat(array.get(0).getType()).isEqualTo(PropertyType.OBJECT);
        List<JsonProperty> object = array.get(0).getProperties();
        assertThat(object).hasSize(2);

        assertThat(object.get(0).getName()).isEqualTo("count");
        assertThat(object.get(0).getType()).isEqualTo(PropertyType.INTEGER);
        assertThat(object.get(0).getProperties()).isEmpty();

        assertThat(object.get(1).getName()).isEqualTo("name");
        assertThat(object.get(1).getType()).isEqualTo(PropertyType.STRING);
        assertThat(object.get(1).getProperties()).isEmpty();
    }

    @Test
    public void testInheritedObjectWithCollidingFieldNames() {
        assertThatThrownBy(() -> generateSchemaProperty(InheritedObject.class)) //
                .isInstanceOf(IllegalStateException.class) //
                .hasMessageStartingWith("field name <name> collision in class");
    }

    @Test
    public void testUnwrappedObjectWithDependencyLoopShouldThrow() {
        assertThatThrownBy(() -> generateSchemaProperty(UnwrappedObject.class)) //
                .isInstanceOf(IllegalStateException.class) //
                .hasMessageStartingWith("recursion detected while unwrapping field <object> in <com.mercateo.common.rest.schemagen.SchemaJsonPropertyGeneratorTest$UnwrappedObject>");
    }

    @Test
    public void testUUIDSchemaGeneration() {
        final JsonProperty jsonProperty = generateSchemaProperty(UUIDSchemaObject.class);

        final JsonProperty idJsonProperty = jsonProperty.getPropertyByName("id");

        assertThat(idJsonProperty.getType()).isEqualTo(PropertyType.STRING);
    }

    @Test
    public void testClassWithComplexType() {
        JsonProperty jsonProperty = generateSchemaProperty(TestClassWithBuiltins.class);

        final JsonProperty timestamp = jsonProperty.getPropertyByName("timestamp");
        assertThat(timestamp.getType()).isEqualTo(PropertyType.INTEGER);

        final JsonProperty value = jsonProperty.getPropertyByName("value");
        assertThat(value.getType()).isEqualTo(PropertyType.NUMBER);
    }

    private JsonProperty generateSchemaProperty(Type type) {
        return generateSchemaProperty(ObjectContext.buildFor(GenericType.of(type)));
    }

    private JsonProperty generateSchemaProperty(ObjectContextBuilder<?> objectContextBuilder) {
        return generateSchemaProperty(objectContextBuilder, new SchemaPropertyContext(CallContext
                .create(), (r, c) -> true)).getRoot();
    }

    private JsonPropertyResult generateSchemaProperty(ObjectContextBuilder<?> objectContextBuilder,
                                                      SchemaPropertyContext context) {
        return schemaGenerator.generateSchemaProperty(objectContextBuilder, context);
    }

    enum TestEnum {
        FOO, BAR
    }

    enum TestEnumJsonValue {
        FOO_ONE, BAR_TWO;

        @JsonValue
        public String getValue() {
            return UPPER_UNDERSCORE.to(LOWER_CAMEL, name());
        }
    }

    public static class TestClassWithBuiltins {
        public Date timestamp;

        public BigDecimal value;
    }

    public static class SchemaObject {
        public String name;

        public Integer count;

        @PathParam("path")
        public String pathParameter;
    }

    public static class SchemaMasterObject {
        public SchemaObject schemaObject;
    }

    public static class SchemaObjectWithEnum {
        public TestEnum mode;
    }

    public static class SchemaObjectWithArray {
        public String[] names;
    }

    public static class SchemaObjectWithGenericArray {
        public List<String>[] names;
    }

    public static class SchemaObjectWithGenericWithArray {
        public List<String[]> names;
    }

    public static class SchemaObjectWithGenericElement {
        public List<Float> values;
    }

    public static class SchemaObjectWithNestedGenericElement {
        public List<List<Float>> values;
    }

    public static class SchemaObjectWithNestedArrayElement {
        public Integer[][] values;
    }

    public static class SchemaObjectWithMapAsObject {
        public Map<String, List<String>> valuesByKey;
    }

    public static class SchemaObjectWithMapAsDict {
        public Map<TestEnum, List<String>> valuesByKey;
    }

    public static class SchemaObjectWithMapAsDictEnumJsonValue {
        public Map<TestEnumJsonValue, List<String>> valuesByKey;
    }

    public static class SchemaObjectWithUnwrappedContent {
        @JsonUnwrapped
        public SchemaObject container;
    }

    public static class SchemaObjectWithIgnoredTypeComponent {
        public Optional<String> optional;
    }

    public static class InheritedSchemaObject extends SchemaObject {
        public Boolean enabled;
    }

    public static class UUIDSchemaObject {
        public UUID id;
    }

    public static class ListObject extends ListResponse<SchemaObject> {
        protected ListObject(List<ObjectWithSchema<SchemaObject>> members, JsonHyperSchema schema) {
            super(members, schema);
        }
    }

    public static class RecursiveSchemaObject {
        public String name;

        public List<RecursiveSchemaObject> children;
    }

    public static class ObjectWithInternalReferences {
        public List<SchemaObject> object1;

        public List<List<SchemaObject>> object2;
    }

    public static abstract class SuperObject {
        public String name;
    }

    public static class InheritedObject extends SuperObject {
        @SuppressWarnings("hiding")
        public String name;
    }

    public static class UnwrappedObject {
        public String name;

        @JsonUnwrapped
        public UnwrappedObject object;
    }

    public static class MessageResponse extends ObjectWithSchema<String> {
        MessageResponse(String object, JsonHyperSchema schema) {
            super(object, schema, null);
        }
    }

    public static class WrappedObject extends ObjectWithSchema<SchemaObject> {
        protected WrappedObject(SchemaObject object, JsonHyperSchema schema) {
            super(object, schema, null);
        }
    }

}