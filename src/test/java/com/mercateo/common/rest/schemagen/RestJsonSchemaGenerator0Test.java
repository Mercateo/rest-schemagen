package com.mercateo.common.rest.schemagen;


import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.BeanParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.annotation.JsonValue;
import com.mercateo.common.rest.schemagen.link.ScopeMethod;
import com.mercateo.common.rest.schemagen.parameter.Parameter;
import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({ "boxing", "unused" })
@RunWith(MockitoJUnitRunner.class)
public class RestJsonSchemaGenerator0Test {

    private RestJsonSchemaGenerator schemaGenerator;

    private FieldCheckerForSchema fieldCheckerForSchema = (o, c) -> true;

    @Before
    public void setUp() throws NoSuchMethodException {
        schemaGenerator = new RestJsonSchemaGenerator();
    }

    @Test
    public void createInputSchema() throws NoSuchMethodException {
        final Method getStrings = getTestResourceMethod("getStrings", int.class, int.class);
        final Optional<String> inputSchema = schemaGenerator.createInputSchema(new ScopeMethod(
                getStrings, new Object[0], TestResource.class) {
        }, fieldCheckerForSchema);
        assertThat(inputSchema.isPresent()).isFalse();
    }

    @Test
    public void createInputSchemaWithQueryParams() {
        final Method getStrings = getTestResourceMethod("getStrings", int.class, int.class);
        final Optional<String> inputSchema = schemaGenerator.createInputSchema(new ScopeMethod(
                getStrings, new Object[] { 100, 50 }, TestResource.class) {
        }, fieldCheckerForSchema);
        assertThat(inputSchema.isPresent()).isFalse();
    }

    @Test
    public void createInputSchemaWithSimpleParam() throws NoSuchMethodException {
        final Method getStrings = getTestResourceMethod("setValue", String.class, boolean.class);
        final Optional<String> inputSchema = schemaGenerator.createInputSchema(new ScopeMethod(
                getStrings, new Object[0], TestResource.class) {
        }, fieldCheckerForSchema);
        assertThat(inputSchema.isPresent()).isTrue();
        assertThat(inputSchema.get()).isEqualTo("{\"type\":\"string\"}");
    }

    @Test
    public void createOutputSchemaWithVoidMethod() throws NoSuchMethodException {
        final Method getStrings = getTestResourceMethod("setValue", String.class, boolean.class);
        final Optional<String> inputSchema = schemaGenerator.createOutputSchema(new ScopeMethod(
                getStrings, new Object[0], TestResource.class) {
        }, fieldCheckerForSchema);
        assertThat(inputSchema.isPresent()).isFalse();
    }

    @Test
    public void createInputSchemaWithMultipleSimpleFormParams() throws NoSuchMethodException {
        final Method getStrings = getTestResourceMethod("setName", String.class, String.class);
        final Optional<String> inputSchema = schemaGenerator.createInputSchema(new ScopeMethod(
                getStrings, new Object[0], TestResource.class) {
        }, fieldCheckerForSchema);
        assertThat(inputSchema.isPresent()).isTrue();
        assertThat(inputSchema.get()).isEqualTo(
                "{\"firstName\":{\"type\":\"string\"},\"lastName\":{\"type\":\"string\"}}");
    }

    @Test
    public void createInputSchemaWithBeanParam() throws NoSuchMethodException {
        final Method getStrings = getTestResourceMethod("paramBean", TestBeanParam.class);
        final Optional<String> inputSchema = schemaGenerator.createInputSchema(new ScopeMethod(
                getStrings, new Object[0], TestResource.class) {
        }, fieldCheckerForSchema);

        assertThat(inputSchema.isPresent()).isTrue();
        assertThat(inputSchema.get()).doesNotContain("pathParam");
    }

    @Test
    public void createInputSchemaWithContextParam() throws NoSuchMethodException {
        final Method getStrings = getTestResourceMethod("context", String.class);
        final Optional<String> inputSchema = schemaGenerator.createInputSchema(new ScopeMethod(
                getStrings, new Object[0], TestResource.class) {
        }, fieldCheckerForSchema);

        assertThat(inputSchema.isPresent()).isFalse();
    }

    @Test
    public void createInputSchemaWithEnumParamAndAllowedValue() throws NoSuchMethodException {
        final Method enumValue = getTestResourceMethod("enumValue", TestEnum.class);
        final Parameter<TestEnum> parameter = Parameter.createContext().builderFor(TestEnum.class)
                .allowValues(TestEnum.FOO_VALUE).build();
        final Optional<String> inputSchema = schemaGenerator.createInputSchema(new ScopeMethod(
                enumValue, new Object[] { parameter.get() }, TestResource.class, parameter
                        .context()) {
        }, fieldCheckerForSchema);

        assertThat(inputSchema.isPresent()).isTrue();
        assertThat(inputSchema.get()).containsIgnoringCase(
                "{\"type\":\"string\",\"enum\":[\"fooValue\"]}");
    }

    @Test
    public void createInputSchemaWithEnumParamAndDefaultValue() throws NoSuchMethodException {
        final Method enumValue = getTestResourceMethod("enumValue", TestEnum.class);
        final Parameter<TestEnum> parameter = Parameter.createContext().builderFor(TestEnum.class)
                .defaultValue(TestEnum.FOO_VALUE).build();
        final Optional<String> inputSchema = schemaGenerator.createInputSchema(new ScopeMethod(
                enumValue, new Object[] { parameter.get() }, TestResource.class, parameter
                        .context()) {
        }, fieldCheckerForSchema);

        assertThat(inputSchema.isPresent()).isTrue();
        assertThat(inputSchema.get()).containsIgnoringCase(
                "{\"type\":\"string\",\"enum\":[\"fooValue\",\"barValue\"],\"default\":\"fooValue\"}");
    }

    @Test
    public void createInputSchemaWithEnumParam() throws NoSuchMethodException {
        final Method enumValue = getTestResourceMethod("enumValue", TestEnum.class);
        final Optional<String> inputSchema = schemaGenerator.createInputSchema(new ScopeMethod(
                enumValue, new Object[] { null }, TestResource.class) {
        }, fieldCheckerForSchema);

        assertThat(inputSchema.isPresent()).isTrue();
        assertThat(inputSchema.get()).containsIgnoringCase(
                "{\"type\":\"string\",\"enum\":[\"fooValue\",\"barValue\"]}");
    }

    private Method getTestResourceMethod(String name, Class<?>... args) {
        try {
            return TestResource.class.getDeclaredMethod(name, args);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public enum TestEnum {
        FOO_VALUE, BAR_VALUE;

        @JsonValue
        public String getValue() {
            return toCamelCaseFirstLowerCase(this);
        }
    }

    public static class TestBeanParam {
        @DefaultValue("5")
        @QueryParam("size")
        private final Integer size;

        @QueryParam("fields")
        private List<String> fields;

        @PathParam("pathParam")
        private String pathParam;

        public TestBeanParam(List<String> fields, int size) {
            this.fields = fields;
            this.size = size;
        }

    }

    @Path("/home")
    public class TestResource {

        @GET
        @Path("/at")
        public String[] getStrings(@QueryParam("offset") int offset,
                @QueryParam("limit") int limit) {
            return null;
        }

        @PUT
        @Path("/foo")
        public void setValue(String name, @QueryParam("debug") boolean debug) {
            // Nothing to do.
        }

        @POST
        @Path("/bar")
        public void setName(@FormParam("firstName") String firstName,
                @FormParam("lastName") String lastName) {
            // Nothing to do.
        }

        @PUT
        @Path("/of")
        public void paramBean(@BeanParam TestBeanParam beanParam) {
            // Nothing to do.
        }

        @PUT
        @Path("/ofContext")
        public void context(@Context String bla) {
            // Nothing to do.
        }

        @GET
        @Path("/enumValue")
        public void enumValue(TestEnum enumValue) {
            // Nothing to do
        }
    }

    public static String toCamelCaseFirstLowerCase(Enum<? extends Enum<?>> enumInstance) {
        return toCamelCase(enumInstance, false);
    }

    private static String toCamelCase(Enum<? extends Enum<?>> enumInstance,
            final boolean initialUpperCase) {

        if (enumInstance == null) {
            return null;
        }

        final String roleName = enumInstance.name().toLowerCase();

        final StringBuilder sb = new StringBuilder();

        boolean toUppercase = initialUpperCase;
        for (char c : roleName.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                if (toUppercase) {
                    sb.append(Character.toUpperCase(c));
                    toUppercase = false;
                } else {
                    sb.append(c);
                }
            } else {
                toUppercase = true;
            }
        }
        return sb.toString();
    }
}