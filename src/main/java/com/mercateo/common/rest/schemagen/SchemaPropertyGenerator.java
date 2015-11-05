package com.mercateo.common.rest.schemagen;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.collect.ImmutableMap;
import com.mercateo.common.rest.schemagen.generator.ObjectContext;
import com.mercateo.common.rest.schemagen.generator.PathContext;
import com.mercateo.common.rest.schemagen.generictype.GenericType;

public class SchemaPropertyGenerator {

    private static final Map<Class<?>, PropertyBuilder> builtins = ImmutableMap.of(BigDecimal.class,
            Property.builderFor(BigDecimal.class).withChildren( //
                    Property.builderFor(Integer.class).withName("scale").setRequired().build(), //
                    Property.builderFor(Integer.class).withName("precision").setRequired().build()), //
            Date.class, Property.builderFor(Integer.class).setRequired());

    /**
     * Generate and return a property hierarchy for the object defined by the
     * given
     * {@link com.mercateo.common.rest.schemagen.generator.ObjectContext.Builder}
     * {@code objectContextBuilder}
     *
     * @param objectContextBuilder
     *            the object context builder for the object to build the
     *            property hierarchy for
     * @param context
     * @return property hierarchy
     */
    public Property generateSchemaProperty(ObjectContext.Builder<?> objectContextBuilder,
            SchemaPropertyContext context) {
        return generateSchemaProperty(objectContextBuilder.build(), context);
    }

    /**
     * Generate and return a property hierarchy for the object defined by the
     * given {@link ObjectContext} {@code objectContext}
     *
     * @param objectContext
     *            the object context for the object to build the property
     *            hierarchy for
     * @param context
     * @return property hierarchy
     */
    public Property generateSchemaProperty(ObjectContext<?> objectContext,
            SchemaPropertyContext context) {
        final Property property = determineProperty("#", objectContext, new PathContext(), context);
        enableIdsOfReferencedElements(property);
        return Property.builderFor(objectContext).withProperty(property).withName(objectContext
                .getRawType().getSimpleName()).build();
    }

    private void enableIdsOfReferencedElements(Property property) {
        final HashMap<String, Property> pathMap = new HashMap<>();
        collectIdsOfReferencedElements(property, pathMap);
        enableIdsOfReferencedElements(property, pathMap);
    }

    private void collectIdsOfReferencedElements(final Property property, final Map<String, Property> pathMap) {
        pathMap.put(property.getPath(), property);

        for (Property childProperty : property.getProperties()) {
            collectIdsOfReferencedElements(childProperty, pathMap);
        }
    }

    private void enableIdsOfReferencedElements(final Property property, final Map<String, Property> pathMap) {
        if (property.getRef() != null) {
            final Property referencedProperty = pathMap.get(property.getRef());
            if (referencedProperty == null) {
                throw new IllegalStateException("There is an reference id (+" + property.getRef()
                        + "), but no referenced object for it");
            }
            referencedProperty.enableId();
        }

        for (Property childProperty : property.getProperties()) {
            enableIdsOfReferencedElements(childProperty, pathMap);
        }
    }

    private List<Property> getProperties(ObjectContext<?> objectContext, PathContext pathContext,
            SchemaPropertyContext context) {
        Map<String, Property> propertiesByName = new TreeMap<>();
        do {
            final List<Property> typeLevelProperties = getTypeLevelProperties(objectContext,
                    pathContext, context);
            for (Property typeLevelProperty : typeLevelProperties) {
                final String name = typeLevelProperty.getName();
                if (!propertiesByName.containsKey(name)) {
                    propertiesByName.put(name, typeLevelProperty);
                } else {
                    throw new IllegalStateException("field name <" + name + "> collision in class "
                            + objectContext.getRawType().getSimpleName());
                }
            }
        } while ((objectContext = objectContext.forSupertype()) != null);

        return new ArrayList<>(propertiesByName.values());
    }

    private List<Property> getTypeLevelProperties(ObjectContext<?> objectContext,
            PathContext pathContext, SchemaPropertyContext context) {
        List<Property> levelProperties = new ArrayList<>();
        final GenericType<?> type = objectContext.getType();
        for (Field field : sortByName(type.getDeclaredFields())) {
            if (objectContext.isApplicable(field, context)) {
                if (field.getAnnotation(JsonUnwrapped.class) != null) {
                    levelProperties.addAll(getUnwrappedProperties(objectContext.forField(field),
                            pathContext, context));
                } else {
                    levelProperties.add(determineProperty(field.getName(), objectContext.forField(
                            field), pathContext, context));
                }
            }
        }
        return levelProperties;
    }

    private Field[] sortByName(Field[] fields) {
        return Stream.of(fields).sorted((f1, f2) -> f1.getName().compareTo(f2.getName())).toArray(
                Field[]::new);
    }

    private List<Property> getUnwrappedProperties(ObjectContext<?> objectContext,
            PathContext pathContext, SchemaPropertyContext context) {
        final PropertyType unwrappedFieldType = objectContext.getPropertyType();
        if (PropertyType.PRIMITIVE_TYPES.contains(unwrappedFieldType)) {
            throw new IllegalStateException("can not unwrap primitive type " + unwrappedFieldType);
        }
        return getProperties(objectContext, pathContext, context);
    }

    private Property determineProperty(String name, ObjectContext<?> objectContext,
            PathContext pathContext, SchemaPropertyContext context) {
        if (builtins.containsKey(objectContext.getRawType())) {
            return builtins.get(objectContext.getRawType()).withName(name).build();
        } else {
            return determineProperty(Property.builderFor(objectContext).withName(name),
                    objectContext, pathContext, context);
        }
    }

    private Property determineProperty(PropertyBuilder builder, ObjectContext<?> objectContext,
            PathContext pathContext, SchemaPropertyContext context) {
        final Class<?> rawType = objectContext.getRawType();

        if (pathContext.isKnown(rawType)) {
            return builder.withPath(pathContext.getCurrentPath() + "/" + builder.getName())
                    .withRef(pathContext.getPath(rawType)).build();
        }

        final PropertyType propertyType = objectContext.getPropertyType();
        pathContext = pathContext.enter(builder.getName(), propertyType == PropertyType.OBJECT
                ? rawType : null);
        return builder.withPath(pathContext.getCurrentPath()).withChildren(getNestedProperties(
                objectContext, pathContext, context)).build();
    }

    private List<Property> getNestedProperties(ObjectContext<?> objectContext,
            PathContext pathContext, SchemaPropertyContext context) {
        List<Property> properties = new ArrayList<>();
        switch (objectContext.getPropertyType()) {
        case OBJECT:
            properties = getProperties(objectContext, pathContext, context);
            break;

        case ARRAY:
            properties.add(determineProperty("", objectContext.getContained(), pathContext,
                    context));
            break;

        default:
            break;
        }
        return properties;
    }

}
