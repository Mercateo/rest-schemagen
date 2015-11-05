package com.mercateo.common.rest.schemagen.parameter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.assertj.core.api.StrictAssertions.assertThatThrownBy;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

public class Parameter0Test {

    private Parameter.Builder<TestClass> builder;

    @Before
    public void setUp() {
        builder = Parameter.builderFor(TestClass.class);
    }

    @Test
    public void testIsEmptyWithoutArg() {
        assertThat(builder.isEmpty()).isTrue();
    }

    @Test
    public void testIsEmptyWithSingleAllowedArg() {
        builder.allowValues(new TestClass());

        assertThat(builder.isEmpty()).isFalse();
    }

    @Test
    public void testIsEmptyWithDefaultArg() {
        builder.defaultValue(new TestClass());

        assertThat(builder.isEmpty()).isFalse();
    }

    @Test
    public void testAllowValuesWithMapper() {

        TestClass testClass = new TestClass();

        final Parameter<TestClass> parameter = builder.allowValues(Collections.singletonList("foo"),
                s -> {
                    assertThat(s).isEqualTo("foo");
                    return testClass;
                }).build();

        assertThat(parameter.get()).isSameAs(testClass);
    }

    @Test
    public void testBuildThrowsWithNoArg() {
        assertThatThrownBy(builder::build).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(
                        "parameter should have at least one allowed or default value set");
    }

    @Test
    public void testBuildThrowsWithTwoIdenticalParameters() {
        TestClass testClass = new TestClass();

        builder.allowValues(testClass);
        builder.build();

        assertThatThrownBy(builder::build).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("parameter values can only be used once");
    }

    @Test
    public void testContainerHasParameter() {
        TestClass testClass = new TestClass();
        final Parameter<TestClass> parameter = builder.allowValues(testClass).build();

        final CallContext context = parameter.context();
        assertThat(context.hasParameter(testClass)).isTrue();
        assertThat(context.hasParameter(new TestClass())).isFalse();
        assertThat(context.getParameter(testClass)).isSameAs(parameter);
        assertThat(context.isEmpty()).isFalse();
    }

    @Test
    public void testBuilderIsEmptyIfAtLeastOneAllowValuesCallHasNoArguments() {
        TestClass testClass = new TestClass();
        builder.allowValues(testClass).allowValues();

        assertThat(builder.isEmpty()).isTrue();
    }

    @Test
    public void testParameterWithAllowedValuesOnly() {
        TestClass testClass = new TestClass();
        final Parameter<TestClass> parameter = builder.allowValues(testClass).build();

        assertThat(parameter.hasAllowedValues()).isTrue();
        assertThat(parameter.getAllowedValues()).containsExactly(testClass);
        assertThat(parameter.hasDefaultValue()).isFalse();
    }

    @Test
    public void testParameterWithDefaultValuesOnly() {
        TestClass testClass = new TestClass();
        final Parameter<TestClass> parameter = builder.defaultValue(testClass).build();

        assertThat(parameter.hasDefaultValue()).isTrue();
        assertThat(parameter.getDefaultValue()).isSameAs(testClass);
        assertThat(parameter.hasAllowedValues()).isFalse();
    }

    @Test
    public void testCreateContext() {
        final CallContext context = Parameter.createContext();

        assertThat(context).isNotNull();
        assertThat(context.isEmpty()).isTrue();
    }

    static class TestClass {

    }

}