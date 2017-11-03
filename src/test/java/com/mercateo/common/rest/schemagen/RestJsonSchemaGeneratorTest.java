package com.mercateo.common.rest.schemagen;


import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.net.URL;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.ws.rs.BeanParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.annotation.JsonValue;
import com.mercateo.common.rest.schemagen.annotation.Media;
import com.mercateo.common.rest.schemagen.link.CallScope;
import com.mercateo.common.rest.schemagen.parameter.CallContext;
import com.mercateo.common.rest.schemagen.parameter.Parameter;
import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;
import com.mercateo.reflection.Call;

@SuppressWarnings({"boxing", "unused"})
@RunWith(MockitoJUnitRunner.class)
public class RestJsonSchemaGeneratorTest {

    private RestJsonSchemaGenerator schemaGenerator;

    private FieldCheckerForSchema fieldCheckerForSchema = (o, c) -> true;

    @Before
    public void setUp() throws NoSuchMethodException {
        schemaGenerator = new RestJsonSchemaGenerator();
    }

    @Test
    public void createInputSchema() throws NoSuchMethodException {
        final Method getStrings = getTestResourceMethod("getStrings", int.class, int.class);
        final Optional<String> inputSchema = schemaGenerator.createInputSchema(new CallScope(TestResource.class,
				getStrings, new Object[0], null), fieldCheckerForSchema);
        assertThat(inputSchema.isPresent()).isFalse();
    }

	@Test
	public void createInputSchemaForBuiltins() throws NoSuchMethodException {
		final Method dateTime = getTestResourceMethod("dateTime", DateTimeParam.class);
		final Optional<String> inputSchema = schemaGenerator
				.createInputSchema(
						new CallScope(TestResource.class, dateTime, new Object[] { null }, CallContext.create()),
						fieldCheckerForSchema);
		assertThat(inputSchema.get()).isEqualTo(
				"{\"type\":\"object\",\"properties\":{\"date\":{\"type\":\"integer\"},\"offsetDateTime\":{\"type\":\"string\",\"format\":\"date-time\"},\"offsetTime\":{\"type\":\"string\",\"format\":\"full-time\"},\"requiredDate\":{\"type\":\"integer\"},\"url\":{\"type\":\"string\"},\"uuid\":{\"type\":\"string\",\"format\":\"uuid\",\"pattern\":\"^[a-f0-9]{8}-[a-f0-9]{4}-4[a-f0-9]{3}-[89ab][a-f0-9]{3}-[a-f0-9]{12}$\"}},\"required\":[\"offsetDateTime\",\"requiredDate\",\"url\",\"uuid\"]}");
	}

    @Test
    public void createInputSchemaWithQueryParams() {
        final Method getStrings = getTestResourceMethod("getStrings", int.class, int.class);
        final Optional<String> inputSchema = schemaGenerator.createInputSchema(new CallScope(TestResource.class,
				getStrings, new Object[] { 100, 50 }, null), fieldCheckerForSchema);
        assertThat(inputSchema.isPresent()).isFalse();
    }

    @Test
    public void createInputSchemaWithHeaderParams() {
        final Method withHeader = Call.methodOf(TestResource.class, r -> r.withHeader(null, null));
        final Optional<String> inputSchema = schemaGenerator.createInputSchema(new CallScope(TestResource.class,
				withHeader, new Object[] { "", "" }, CallContext.create()), fieldCheckerForSchema);
        assertThat(inputSchema.isPresent()).isTrue();
    }

    @Test
    public void createInputSchemaWithSimpleParam() throws NoSuchMethodException {
        final Method getStrings = getTestResourceMethod("setValue", String.class, boolean.class);
        final Optional<String> inputSchema = schemaGenerator.createInputSchema(new CallScope(TestResource.class,
				getStrings, new Object[0], CallContext.create()), fieldCheckerForSchema);
        assertThat(inputSchema.isPresent()).isTrue();
        assertThat(inputSchema.get()).isEqualTo("{\"type\":\"string\"}");
    }

    @Test
    public void createInputSchemaWithSimpleParamAndDefaultValue() throws NoSuchMethodException {
        final Method getStrings = getTestResourceMethod("setValue", String.class, boolean.class);
        final CallContext callContext = CallContext.create();
        final Parameter<String> parameter = callContext.builderFor(String.class)
                .defaultValue("defaultValue").build();
        final Optional<String> inputSchema = schemaGenerator.createInputSchema(new CallScope(TestResource.class,
				getStrings, new Object[] { parameter.get() }, callContext), fieldCheckerForSchema);
        assertThat(inputSchema.isPresent()).isTrue();
        assertThat(inputSchema.get()).isEqualTo("{\"type\":\"string\",\"default\":\"defaultValue\"}");
    }

    @Test
    public void createInputSchemaWithSimpleParamAndAllowedValues() throws NoSuchMethodException {
        final Method getStrings = getTestResourceMethod("setValue", String.class, boolean.class);
        final CallContext callContext = CallContext.create();
        final Parameter<String> parameter = callContext.builderFor(String.class)
                .allowValues("foo", "bar", "baz").build();
        final Optional<String> inputSchema = schemaGenerator.createInputSchema(new CallScope(TestResource.class,
				getStrings, new Object[] { parameter.get() }, callContext), fieldCheckerForSchema);
        assertThat(inputSchema.isPresent()).isTrue();
        assertThat(inputSchema.get()).isEqualTo("{\"type\":\"string\",\"enum\":[\"bar\",\"foo\",\"baz\"]}");
    }

    @Test
    public void createOutputSchemaWithVoidMethod() throws NoSuchMethodException {
        final Method getStrings = getTestResourceMethod("setValue", String.class, boolean.class);
        final Optional<String> outputSchema = schemaGenerator.createOutputSchema(new CallScope(TestResource.class,
				getStrings, new Object[0], null), fieldCheckerForSchema);
        assertThat(outputSchema.isPresent()).isFalse();
    }

    @Test
    public void createInputSchemaWithMultipleSimpleFormParams() throws NoSuchMethodException {
        final Method getStrings = getTestResourceMethod("setName", String.class, String.class);
        final Optional<String> inputSchema = schemaGenerator.createInputSchema(new CallScope(TestResource.class,
				getStrings, new Object[0], CallContext.create()), fieldCheckerForSchema);
        assertThat(inputSchema.isPresent()).isTrue();
        assertThat(inputSchema.get()).isEqualTo(
                "{\"firstName\":{\"type\":\"string\"},\"lastName\":{\"type\":\"string\"}}");
    }

    @Test
    public void createInputSchemaWithBeanParam() throws NoSuchMethodException {
        final Method getStrings = getTestResourceMethod("paramBean", TestBeanParam.class);
        final Optional<String> inputSchema = schemaGenerator.createInputSchema(new CallScope(TestResource.class,
				getStrings, new Object[0], CallContext.create()), fieldCheckerForSchema);

        assertThat(inputSchema.isPresent()).isTrue();
        assertThat(inputSchema.get()).doesNotContain("pathParam");
    }

    @Test
    public void createInputSchemaWithContextParam() throws NoSuchMethodException {
        final Method getStrings = getTestResourceMethod("context", String.class);
        final Optional<String> inputSchema = schemaGenerator.createInputSchema(new CallScope(TestResource.class,
				getStrings, new Object[0], null), fieldCheckerForSchema);

        assertThat(inputSchema.isPresent()).isFalse();
    }

    @Test
    public void createInputSchemaWithEnumParamAndAllowedValue() throws NoSuchMethodException {
        final Method enumValue = getTestResourceMethod("enumValue", TestEnum.class);
        final Parameter<TestEnum> parameter = Parameter.createContext().builderFor(TestEnum.class)
                .allowValues(TestEnum.FOO_VALUE).build();
        final Optional<String> inputSchema = schemaGenerator.createInputSchema(new CallScope(TestResource.class,
				enumValue, new Object[] { parameter.get() }, parameter.context()), fieldCheckerForSchema);

        assertThat(inputSchema.isPresent()).isTrue();
        assertThat(inputSchema.get()).containsIgnoringCase(
                "{\"type\":\"string\",\"enum\":[\"FOO_VALUE\"]}");
    }

    @Test
    public void createInputSchemaWithEnumParamAndDefaultValue() throws NoSuchMethodException {
        final Method enumValue = getTestResourceMethod("enumValue", TestEnum.class);
        final Parameter<TestEnum> parameter = Parameter.createContext().builderFor(TestEnum.class)
                .defaultValue(TestEnum.FOO_VALUE).build();
        final Optional<String> inputSchema = schemaGenerator.createInputSchema(new CallScope(TestResource.class,
                enumValue, new Object[]{parameter.get()}, parameter
						.context()),
				fieldCheckerForSchema);

        assertThat(inputSchema.isPresent()).isTrue();
        assertThat(inputSchema.get()).containsIgnoringCase(
                "\"type\":\"string\"");
        assertThat(inputSchema.get()).containsIgnoringCase(
                "\"enum\":[\"FOO_VALUE\",\"BAR_VALUE\"]");
        assertThat(inputSchema.get()).containsIgnoringCase(
                "\"default\":\"FOO_VALUE\"");
    }

    @Test
    public void createInputSchemaWithEnumParam() throws NoSuchMethodException {
        final Method enumValue = getTestResourceMethod("enumValue", TestEnum.class);
        final Optional<String> inputSchema = schemaGenerator.createInputSchema(new CallScope(TestResource.class,
				enumValue, new Object[] { null }, CallContext.create()), fieldCheckerForSchema);

        assertThat(inputSchema.isPresent()).isTrue();
        assertThat(inputSchema.get()).containsIgnoringCase(
                "\"type\":\"string\"");
        assertThat(inputSchema.get()).containsIgnoringCase(
                "\"enum\":[\"FOO_VALUE\",\"BAR_VALUE\"]");
    }

    @Test
    public void createInputSchemaWithEnumParamWithJsonValue() throws NoSuchMethodException {
        final Method enumValue = getTestResourceMethod("enumValueJsonValue", TestEnumJsonValue.class);
        final Optional<String> inputSchema = schemaGenerator.createInputSchema(new CallScope(TestResource.class,
				enumValue, new Object[] { null }, CallContext.create()), fieldCheckerForSchema);

        assertThat(inputSchema).isPresent();
        assertThat(inputSchema.get()).containsIgnoringCase(
                "{\"type\":\"string\",\"enum\":[\"fooValue\",\"barValue\"]}");
    }

    @Test
    public void createInputSchemaWithMediaParam() {
        final Method enumValue = getTestResourceMethod("media", String.class);
        final Optional<String> inputSchema = schemaGenerator.createInputSchema(new CallScope(TestResource.class,
				enumValue, new Object[] { null }, CallContext.create()), fieldCheckerForSchema);

        assertThat(inputSchema.isPresent()).isTrue();
        assertThat(inputSchema.get()).containsIgnoringCase(
                "{\"type\":\"string\",\"mediaType\":\"<type>\",\"binaryEncoding\":\"<binaryEncoding>\"}");
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
    }

    public enum TestEnumJsonValue {
        FOO_VALUE, BAR_VALUE;

        @JsonValue
        public String getValue() {
            return UPPER_UNDERSCORE.to(LOWER_CAMEL, name());
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

	public static class DateTimeParam {
		@NotNull
		private OffsetDateTime offsetDateTime;
		private OffsetTime offsetTime;

		@NotNull
		private URL url;

		private Date date;

		@NotNull
		private Date requiredDate;

		@NotNull
		private UUID uuid;

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

        @GET
        @Path("/enumValueJsonValue")
        public void enumValueJsonValue(TestEnumJsonValue enumValue) {
            // Nothing to do
        }

        @GET
        @Path("/media")
        public void media(@Media(type = "<type>", binaryEncoding = "<binaryEncoding>") String media) {
            // Nothing to do.
        }

        @POST
        @Path("/withheader")
        public void withHeader(String body, @HeaderParam("key") String values) {
            // Nothing to do.
        }

		@POST
		@Path("datetime")
		public void dateTime(DateTimeParam dateTimeParam) {
			// Nothing to do.
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