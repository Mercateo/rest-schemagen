package com.mercateo.common.rest.schemagen;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonValue;
import com.mercateo.common.rest.schemagen.generator.ObjectContext;
import com.mercateo.common.rest.schemagen.generictype.GenericType;
import com.mercateo.common.rest.schemagen.plugin.IndividualSchemaGenerator;

public class PropertyBuilder {

    private String name;

    private Class<?> rawType;

    private PropertyType propertyType;

    private List<Property> childProperties;

    private String defaultValue;

    private List<String> allowedValues;

    private boolean required;

    private SizeConstraints sizeConstraints = SizeConstraints.empty();

    private ValueConstraints valueConstraints = ValueConstraints.empty();

    private Class<? extends IndividualSchemaGenerator> schemaGenerator;

    private String ref;

    private String path;

    public PropertyBuilder() {
        childProperties = new ArrayList<>();
    }

    public PropertyBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public PropertyBuilder withPath(String path) {
        this.path = path;
        return this;
    }

    public PropertyBuilder withRef(String ref) {
        this.ref = ref;
        return this;
    }

    public PropertyBuilder withType(GenericType<?> type) {
        this.rawType = type.getRawType();
        return this;
    }

    public PropertyBuilder withType(Class<?> clazz) {
        this.rawType = clazz;
        return this;
    }

    public PropertyBuilder withPropertyType(PropertyType type) {
        this.propertyType = type;
        return this;
    }

    public PropertyBuilder withChildren(Property... childProperties) {
        return withChildren(Arrays.asList(childProperties));
    }

    public PropertyBuilder withChildren(List<Property> childProperties) {
        this.childProperties = childProperties;
        return this;
    }

    public PropertyBuilder withDefaultValue(Object defaultValue) {
        if (defaultValue != null) {
            this.defaultValue = convertToString(defaultValue);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    private String convertToString(Object defaultValue) {
        if (Enum.class.isAssignableFrom(rawType)) {
            return convertEnumToString((Enum<? extends Enum<?>>) defaultValue);
        } else {
            return defaultValue.toString();
        }
    }

    public PropertyBuilder withAllowedValues(List<? extends Object> allowedValues) {
        if (allowedValues != null) {
            final List<String> newAllowedValues = allowedValues.stream().filter(x -> x != null).map(
                    this::convertToStrings).flatMap(List::stream).collect(Collectors.toList());
            this.allowedValues = removeDuplicates(newAllowedValues);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    private List<String> convertToStrings(Object x) {
        if (Enum.class.isAssignableFrom(rawType)) {
            return Collections.singletonList(convertEnumToString((Enum<? extends Enum<?>>) x));
        } else {
            return Arrays.asList(x.toString());
        }
    }

    private String convertEnumToString(Enum<? extends Enum<?>> x) {
        final Optional<Method> valueMethod = Stream.of(x.getClass().getDeclaredMethods()).filter(
                m -> m.isAnnotationPresent(JsonValue.class)).findFirst();

        if (valueMethod.isPresent()) {
            try {
                return valueMethod.get().invoke(x).toString();
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else {
            return x.name();
        }
    }

    private ArrayList<String> removeDuplicates(final List<String> newAllowedValues) {
        return new ArrayList<>(new HashSet<>(newAllowedValues));
    }

    public PropertyBuilder setRequired() {
        required = true;
        return this;
    }

    private PropertyBuilder withRequired(boolean required) {
        this.required = required;
        return this;
    }

    public PropertyBuilder withSizeConstraints(SizeConstraints sizeConstraints) {
        this.sizeConstraints = sizeConstraints;
        return this;
    }

    public PropertyBuilder withValueConstraints(ValueConstraints valueConstraints) {
        this.valueConstraints = valueConstraints;
        return this;
    }

    public PropertyBuilder withContext(ObjectContext<?> objectContext) {
        withType(objectContext.getType());
        withPropertyType(objectContext.getPropertyType());
        withDefaultValue(objectContext.getDefaultValue());
        withAllowedValues(objectContext.getAllowedValues());
        withRequired(objectContext.isRequired());
        withSizeConstraints(objectContext.getSizeConstraints());
        withValueConstraints(objectContext.getValueConstraints());
        withSchemaGenerator(objectContext.getSchemaGenerator());
        return this;
    }

    public PropertyBuilder withProperty(Property property) {
        withName(property.getName());
        withPropertyType(property.getType());
        withPath(property.getPath());
        withRef(property.getRef());
        defaultValue = property.getDefaultValue();
        withRequired(property.isRequired());
        withSizeConstraints(property.getSizeConstraints());
        withValueConstraints(property.getValueConstraints());
        withSchemaGenerator(property.getGenerator());
        allowedValues = property.getAllowedValues();
        withChildren(property.getProperties());
        return this;
    }

    public PropertyBuilder withSchemaGenerator(
            Class<? extends IndividualSchemaGenerator> schemaGenerator) {
        this.schemaGenerator = schemaGenerator;
        return this;
    }

    public Property build() {

        return new Property(name, propertyType, path, ref, defaultValue, getAllowedValues(),
                required, sizeConstraints, valueConstraints, schemaGenerator, childProperties);
    }

    public String getName() {
        return name;
    }

    @SuppressWarnings("unchecked")
    private List<String> getAllowedValues() {
        if (this.allowedValues != null && !this.allowedValues.isEmpty()) {
            return this.allowedValues;
        } else if (Enum.class.isAssignableFrom(rawType)) {
            return Stream.of(rawType.getEnumConstants()).map(e -> convertEnumToString(
                    (Enum<? extends Enum<?>>) e)).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
