package com.mercateo.common.rest.schemagen;

import java.util.List;
import java.util.NoSuchElementException;

import com.google.common.annotations.VisibleForTesting;
import com.mercateo.common.rest.schemagen.generator.ObjectContext;
import com.mercateo.common.rest.schemagen.plugin.IndividualSchemaGenerator;

public class Property {

    private final String name;

    private final PropertyType type;

    private final String defaultValue;

    private final boolean isRequired;

    private final List<Property> properties;

    private final Class<? extends IndividualSchemaGenerator> generator;

    private final List<String> allowedValues;

    private transient SizeConstraints sizeConstraints;

    private transient ValueConstraints valueConstraints;

    private transient String ref;

    private transient boolean idEnabled = false;

    private String path;

    Property(String name, PropertyType type, String path, String ref, String defaultValue,
             List<String> allowedValues, boolean isRequired, SizeConstraints sizeConstraints,
             ValueConstraints valueConstraints, Class<? extends IndividualSchemaGenerator> generator, List<Property> children) {
        this.name = name;
        this.type = type;
        this.path = path;
        this.defaultValue = defaultValue;
        this.allowedValues = allowedValues;
        this.isRequired = isRequired;
        this.properties = children;
        this.generator = generator;
        this.ref = ref;
        this.valueConstraints = valueConstraints;
        this.sizeConstraints = sizeConstraints;
    }

    public static PropertyBuilder builderFor(ObjectContext<?> objectContext) {
        return new PropertyBuilder().withContext(objectContext);
    }

    public static PropertyBuilder builderFor(Class<?> clazz) {
        return new PropertyBuilder().withType(clazz).withPropertyType(PropertyTypeMapper.of(clazz));
    }

    public String getId() {
        return idEnabled ? path : null;
    }

    public void enableId() {
        idEnabled = true;
    }

    public final String getName() {
        return name;
    }

    public final PropertyType getType() {
        return type;
    }

    public final String getRef() {
        return ref;
    }

    public final String getDefaultValue() {
        return defaultValue;
    }

    public final boolean isRequired() {
        return isRequired;
    }

    public final List<Property> getProperties() {
        return properties;
    }

    public final SizeConstraints getSizeConstraints() {
        return sizeConstraints;
    }

    public final ValueConstraints getValueConstraints() {
        return valueConstraints;
    }

    @VisibleForTesting
    public final Property getPropertyByName(String name) {
        for (Property p : properties) {
            if (p.getName().equals(name))
                return p;
        }
        throw new NoSuchElementException(String.format("[%s] does not have a property named  '%s'",
                this, name));
    }

    public Class<? extends IndividualSchemaGenerator> getGenerator() {
        return generator;
    }

    @Override
    public String toString() {
        return "Property [name=" + name + ", type=" + type + ", defaultValue=" + defaultValue
                + ", isRequired=" + isRequired + ", children=" + properties + "]";
    }

    public List<String> getAllowedValues() {
        return allowedValues;
    }

    public String getPath() {
        return path;
    }
}
