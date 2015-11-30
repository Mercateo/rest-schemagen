package com.mercateo.common.rest.schemagen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.ws.rs.PathParam;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.collect.ImmutableList;
import com.mercateo.common.rest.schemagen.generator.ObjectContext;
import com.mercateo.common.rest.schemagen.generictype.GenericType;
import com.mercateo.common.rest.schemagen.parameter.CallContext;
import com.mercateo.common.rest.schemagen.types.ListResponse;
import com.mercateo.common.rest.schemagen.types.ObjectWithSchema;

@RunWith(MockitoJUnitRunner.class)
public class SchemaPropertyGeneratorTest {

    private SchemaPropertyGenerator schemaGenerator;

    @Before
    public void setUp() {
        schemaGenerator = new SchemaPropertyGenerator();
    }

    @Test
    public void testString() {
        Property property = generateSchemaProperty(String.class);
        assertThat(property.getName()).isEqualTo("String");
        assertThat(property.getType()).isEqualTo(PropertyType.STRING);
        assertThat(property.getProperties()).isEmpty();
    }

    @Test
    public void testObject() {
        Property property = generateSchemaProperty(SchemaObject.class);
        assertThat(property.getName()).isEqualTo("SchemaObject");
        assertThat(property.getType()).isEqualTo(PropertyType.OBJECT);
        List<Property> properties = property.getProperties();
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

        final Property property = generateSchemaProperty(ObjectContext.buildFor(SchemaObject.class)
                .withDefaultValue(defaultValue));

        List<Property> properties = property.getProperties();
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

        final Property property = generateSchemaProperty(ObjectContext.buildFor(SchemaObject.class)
                .withAllowedValue(allowedValues));

        List<Property> properties = property.getProperties();
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

        final Property property = generateSchemaProperty(ObjectContext.buildFor(SchemaObject.class)
                .withCurrentValue(currentValue));

        List<Property> properties = property.getProperties();
        assertThat(properties).hasSize(3);
        assertThat(properties.get(2).getName()).isEqualTo("pathParameter");
    }

    @Test
    public void testObjectCurrentValuesAndPathParamsSet() {
        SchemaObject currentValue = new SchemaObject();
        currentValue.pathParameter = "path";

        final Property property = generateSchemaProperty(ObjectContext.buildFor(SchemaObject.class)
                .withCurrentValue(currentValue));

        List<Property> properties = property.getProperties();
        assertThat(properties).hasSize(2);

    }

    @Test
    public void testObjectAllowedAndDefaultValues() {
        SchemaObject allowedValues = new SchemaObject();
        allowedValues.name = "foo|bar|baz";

        SchemaObject defaultValue = new SchemaObject();
        defaultValue.name = "baz";

        final Property property = generateSchemaProperty(ObjectContext.buildFor(SchemaObject.class)
                .withAllowedValue(allowedValues).withDefaultValue(defaultValue));

        List<Property> properties = property.getProperties();
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

        final Property property = generateSchemaProperty(ObjectContext.buildFor(SchemaObject.class)
                .withAllowedValues(allowedValues));

        List<Property> properties = property.getProperties();
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

        final Property property = generateSchemaProperty(ObjectContext.buildFor(
                SchemaMasterObject.class).withDefaultValue(defaultValue));

        List<Property> properties = property.getProperties();
        assertThat(properties).hasSize(1);

        List<Property> nestedProperties = property.getProperties();
        assertThat(nestedProperties).hasSize(1);

        List<Property> nestedObjectProperties = nestedProperties.get(0).getProperties();
        assertThat(nestedObjectProperties).hasSize(2);

        assertThat(nestedObjectProperties.get(0).getName()).isEqualTo("count");
        assertThat(nestedObjectProperties.get(0).getDefaultValue()).isEqualTo("5");

        assertThat(nestedObjectProperties.get(1).getName()).isEqualTo("name");
        assertThat(nestedObjectProperties.get(1).getDefaultValue()).isNull();
    }

    @Test
    public void testObjectWithEnumDefaultAllowedValues() {
        Property property = generateSchemaProperty(SchemaObjectWithEnum.class);
        List<Property> properties = property.getProperties();
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

        Property property = generateSchemaProperty(ObjectContext.buildFor(
                SchemaObjectWithEnum.class).withAllowedValue(allowedValue));
        List<Property> properties = property.getProperties();
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

        Property property = generateSchemaProperty(ObjectContext.buildFor(
                SchemaObjectWithEnum.class).withDefaultValue(defaultValue));
        List<Property> properties = property.getProperties();
        assertThat(properties).hasSize(1);

        assertThat(properties.get(0).getName()).isEqualTo("mode");
        assertThat(properties.get(0).getType()).isEqualTo(PropertyType.STRING);
        assertThat(properties.get(0).getAllowedValues()).containsExactly("FOO", "BAR");
        assertThat(properties.get(0).getDefaultValue()).isEqualTo("BAR");
        assertThat(properties.get(0).getProperties()).isEmpty();
    }

    @Test
    public void testObjectWithArray() {
        Property property = generateSchemaProperty(SchemaObjectWithArray.class);
        assertThat(property.getName()).isEqualTo("SchemaObjectWithArray");
        assertThat(property.getType()).isEqualTo(PropertyType.OBJECT);
        List<Property> properties = property.getProperties();
        assertThat(properties).hasSize(1);

        assertThat(properties.get(0).getName()).isEqualTo("names");
        assertThat(properties.get(0).getType()).isEqualTo(PropertyType.ARRAY);
        List<Property> arrayProperties = properties.get(0).getProperties();
        assertThat(arrayProperties).hasSize(1);

        assertThat(arrayProperties.get(0).getName()).isEqualTo("");
        assertThat(arrayProperties.get(0).getType()).isEqualTo(PropertyType.STRING);
        assertThat(arrayProperties.get(0).getProperties()).isEmpty();
    }

    @Test
    public void testObjectWithGenericArray() {
        Property property = generateSchemaProperty(SchemaObjectWithGenericArray.class);
        assertThat(property.getName()).isEqualTo("SchemaObjectWithGenericArray");
        assertThat(property.getType()).isEqualTo(PropertyType.OBJECT);
        List<Property> properties = property.getProperties();
        assertThat(properties).hasSize(1);

        assertThat(properties.get(0).getName()).isEqualTo("names");
        assertThat(properties.get(0).getType()).isEqualTo(PropertyType.ARRAY);
        List<Property> arrayProperties = properties.get(0).getProperties();
        assertThat(arrayProperties).hasSize(1);

        assertThat(arrayProperties.get(0).getName()).isEqualTo("");
        assertThat(arrayProperties.get(0).getType()).isEqualTo(PropertyType.ARRAY);
        List<Property> listProperties = arrayProperties.get(0).getProperties();
        assertThat(listProperties).hasSize(1);

        assertThat(listProperties.get(0).getName()).isEqualTo("");
        assertThat(listProperties.get(0).getType()).isEqualTo(PropertyType.STRING);
        assertThat(listProperties.get(0).getProperties()).isEmpty();
    }

    @Test
    public void testObjectWithGenericWithArray() {
        Property property = generateSchemaProperty(SchemaObjectWithGenericWithArray.class);
        assertThat(property.getName()).isEqualTo("SchemaObjectWithGenericWithArray");
        assertThat(property.getType()).isEqualTo(PropertyType.OBJECT);
        List<Property> properties = property.getProperties();
        assertThat(properties).hasSize(1);

        assertThat(properties.get(0).getName()).isEqualTo("names");
        assertThat(properties.get(0).getType()).isEqualTo(PropertyType.ARRAY);
        List<Property> arrayProperties = properties.get(0).getProperties();
        assertThat(arrayProperties).hasSize(1);

        assertThat(arrayProperties.get(0).getName()).isEqualTo("");
        assertThat(arrayProperties.get(0).getType()).isEqualTo(PropertyType.ARRAY);
        List<Property> listProperties = arrayProperties.get(0).getProperties();
        assertThat(listProperties).hasSize(1);

        assertThat(listProperties.get(0).getName()).isEqualTo("");
        assertThat(listProperties.get(0).getType()).isEqualTo(PropertyType.STRING);
        assertThat(listProperties.get(0).getProperties()).isEmpty();
    }

    @Test
    public void testObjectWithGenericElement() {
        Property property = generateSchemaProperty(SchemaObjectWithGenericElement.class);
        assertThat(property.getName()).isEqualTo("SchemaObjectWithGenericElement");
        assertThat(property.getType()).isEqualTo(PropertyType.OBJECT);
        List<Property> properties = property.getProperties();
        assertThat(properties).hasSize(1);

        assertThat(properties.get(0).getName()).isEqualTo("values");
        assertThat(properties.get(0).getType()).isEqualTo(PropertyType.ARRAY);
        List<Property> arrayProperties = properties.get(0).getProperties();
        assertThat(arrayProperties).hasSize(1);

        assertThat(arrayProperties.get(0).getName()).isEqualTo("");
        assertThat(arrayProperties.get(0).getType()).isEqualTo(PropertyType.FLOAT);
        assertThat(arrayProperties.get(0).getProperties()).isEmpty();
    }

    @Test
    public void testObjectWithNestedArrayElement() {
        Property property = generateSchemaProperty(SchemaObjectWithNestedArrayElement.class);
        assertThat(property.getName()).isEqualTo("SchemaObjectWithNestedArrayElement");
        assertThat(property.getType()).isEqualTo(PropertyType.OBJECT);
        List<Property> properties = property.getProperties();
        assertThat(properties).hasSize(1);

        assertThat(properties.get(0).getName()).isEqualTo("values");
        assertThat(properties.get(0).getType()).isEqualTo(PropertyType.ARRAY);
        List<Property> outerArrayProperties = properties.get(0).getProperties();
        assertThat(outerArrayProperties).hasSize(1);

        assertThat(outerArrayProperties.get(0).getName()).isEqualTo("");
        assertThat(outerArrayProperties.get(0).getType()).isEqualTo(PropertyType.ARRAY);
        List<Property> innerArrayProperties = outerArrayProperties.get(0).getProperties();
        assertThat(innerArrayProperties).hasSize(1);

        assertThat(innerArrayProperties.get(0).getName()).isEqualTo("");
        assertThat(innerArrayProperties.get(0).getType()).isEqualTo(PropertyType.INTEGER);
        assertThat(innerArrayProperties.get(0).getProperties()).isEmpty();
    }

    @Test
    public void testObjectWithUnwrappedContent() {
        Property property = generateSchemaProperty(SchemaObjectWithUnwrappedContent.class);
        assertThat(property.getName()).isEqualTo("SchemaObjectWithUnwrappedContent");
        assertThat(property.getType()).isEqualTo(PropertyType.OBJECT);
        List<Property> properties = property.getProperties();
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
        Property property = generateSchemaProperty(SchemaObjectWithIgnoredTypeComponent.class);
        assertThat(property.getName()).isEqualTo("SchemaObjectWithIgnoredTypeComponent");
        assertThat(property.getType()).isEqualTo(PropertyType.OBJECT);
        List<Property> properties = property.getProperties();
        assertThat(properties).hasSize(1);

        assertThat(properties.get(0).getName()).isEqualTo("optional");
        assertThat(properties.get(0).getType()).isEqualTo(PropertyType.OBJECT);
        List<Property> optionalProperties = properties.get(0).getProperties();
        assertThat(optionalProperties).hasSize(1);

        assertThat(optionalProperties.get(0).getName()).isEqualTo("value");
        assertThat(optionalProperties.get(0).getType()).isEqualTo(PropertyType.STRING);
        assertThat(optionalProperties.get(0).getProperties()).isEmpty();
    }

    @Test
    public void testInheritedObject() {
        Property property = generateSchemaProperty(InheritedSchemaObject.class);
        assertThat(property.getName()).isEqualTo("InheritedSchemaObject");
        assertThat(property.getType()).isEqualTo(PropertyType.OBJECT);
        List<Property> properties = property.getProperties();
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
        Property property = generateSchemaProperty(WrappedObject.class);
        assertThat(property.getName()).isEqualTo("WrappedObject");
        assertThat(property.getType()).isEqualTo(PropertyType.OBJECT);
        List<Property> properties = property.getProperties();
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
        Property property = generateSchemaProperty(RecursiveSchemaObject.class);
        assertThat(property.getName()).isEqualTo("RecursiveSchemaObject");
        assertThat(property.getType()).isEqualTo(PropertyType.OBJECT);
        List<Property> properties = property.getProperties();
        assertThat(properties).hasSize(2);
    }

    @Test
    public void testInheritedGenericObject() {
        Property property = generateSchemaProperty(ListObject.class);
        assertThat(property.getName()).isEqualTo("ListObject");
        assertThat(property.getType()).isEqualTo(PropertyType.OBJECT);
        List<Property> properties = property.getProperties();
        assertThat(properties).hasSize(1);

        assertThat(properties.get(0).getName()).isEqualTo("members");
        assertThat(properties.get(0).getType()).isEqualTo(PropertyType.ARRAY);
        List<Property> array = properties.get(0).getProperties();
        assertThat(array).hasSize(1);

        assertThat(array.get(0).getName()).isEqualTo("");
        assertThat(array.get(0).getType()).isEqualTo(PropertyType.OBJECT);
        List<Property> object = array.get(0).getProperties();
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
                .hasMessageStartingWith("recursion detected while unwrapping field <object> in <com.mercateo.common.rest.schemagen.SchemaPropertyGeneratorTest$UnwrappedObject>");
    }

    @Test
    public void testUUIDSchemaGeneration() {
        final Property property = generateSchemaProperty(UUIDSchemaObject.class);

        final Property idProperty = property.getPropertyByName("id");

        assertThat(idProperty.getType()).isEqualTo(PropertyType.STRING);
    }

    @Test
    public void testClassWithComplexType() {
        Property property = generateSchemaProperty(TestClassWithBuiltins.class);

        final Property timestamp = property.getPropertyByName("timestamp");
        assertThat(timestamp.getType()).isEqualTo(PropertyType.INTEGER);

        final Property value = property.getPropertyByName("value");
        assertThat(value.getType()).isEqualTo(PropertyType.OBJECT);
        final Property valueScale = value.getPropertyByName("scale");
        assertThat(valueScale.getType()).isEqualTo(PropertyType.INTEGER);
        final Property valuePrecision = value.getPropertyByName("precision");
        assertThat(valuePrecision.getType()).isEqualTo(PropertyType.INTEGER);
    }

    private Property generateSchemaProperty(Type type) {
        return generateSchemaProperty(ObjectContext.buildFor(GenericType.of(type)));
    }

    private Property generateSchemaProperty(ObjectContext.Builder<?> objectContextBuilder) {
        return generateSchemaProperty(objectContextBuilder, new SchemaPropertyContext(CallContext
                .create(), (r, c) -> true));
    }

    private Property generateSchemaProperty(ObjectContext.Builder<?> objectContextBuilder,
            SchemaPropertyContext context) {
        return schemaGenerator.generateSchemaProperty(objectContextBuilder, context);
    }

    enum TestEnum {
        FOO, BAR
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
            super(object, schema);
        }
    }

    public static class WrappedObject extends ObjectWithSchema<SchemaObject> {
        protected WrappedObject(SchemaObject object, JsonHyperSchema schema) {
            super(object, schema);
        }
    }

}