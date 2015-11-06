package com.mercateo.common.rest.schemagen;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.collect.ImmutableMap;
import com.mercateo.common.rest.schemagen.generator.ObjectContext;
import com.mercateo.common.rest.schemagen.generator.PathContext;

public class SchemaPropertyGenerator {

    private static final Map<Class<?>, PropertyBuilder> builtins = ImmutableMap
            .of(BigDecimal.class,
                    Property.builderFor(BigDecimal.class).withChildren(
                            //
                            Property.builderFor(Integer.class).withName("scale").setRequired()
                                    .build(), //
                            Property.builderFor(Integer.class).withName("precision").setRequired()
                                    .build()), //
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
        addIdsToReferencedElements(property, new HashMap<>());
        return Property.builderFor(objectContext).withProperty(property).withName(
                objectContext.getRawType().getSimpleName()).build();
    }

    private void addIdsToReferencedElements(Property property, Map<String, Property> pathMap) {
        pathMap.put(property.getPath(), property);

        if (property.getRef() != null) {
            final Property referencedProperty = pathMap.get(property.getRef());
            if (referencedProperty == null) {
                throw new IllegalStateException("There is an reference id (+" + property.getRef()
                        + "), but no referenced object for it");
            }
            referencedProperty.enableId();
        }

        for (Property childProperty : property.getProperties()) {
            addIdsToReferencedElements(childProperty, pathMap);
        }
    }

    private List<Property> getProperties(final ObjectContext<?> objectContext,
            final PathContext pathContext, final SchemaPropertyContext context) {

        final Map<Field, ObjectContext> fieldContextMap = getFieldContextMap(objectContext, context);

        final Set<String> fieldNames = new HashSet<>();
        final Stream<Field> sortedFields = fieldContextMap.keySet().stream().sorted(this::byName);

        return sortedFields.map(
                field -> {
                    String name = field.getName();

                    if (!fieldNames.contains(name)) {
                        fieldNames.add(name);
                    } else {
                        throw new IllegalStateException("field name <" + name + "> collision in class "
                                + objectContext.getRawType().getSimpleName());
                    }
                    return determineProperty(field.getName(), fieldContextMap.get(field).forField(
                            field), pathContext, context);
                }).collect(Collectors.toList());
    }

    private Map<Field, ObjectContext> getFieldContextMap(ObjectContext objectContext,
            SchemaPropertyContext context) {
        return getFieldContextMap(objectContext, new HashMap<>(), context);
    }

    private Map<Field, ObjectContext> getFieldContextMap(ObjectContext objectContext,
            final Map<Field, ObjectContext> fieldContextMap, SchemaPropertyContext context) {
        do {
            for (Field field : objectContext.getType().getDeclaredFields()) {
                addFieldToMap(field, objectContext, fieldContextMap, context);
            }
        } while ((objectContext = objectContext.forSupertype()) != null);
        return fieldContextMap;
    }

    private void addFieldToMap(Field field, ObjectContext objectContext,
            Map<Field, ObjectContext> fieldContextMap, SchemaPropertyContext context) {
        if (objectContext.isApplicable(field, context)) {
            if (fieldContextMap.containsKey(field)) {
                throw new IllegalStateException("found duplicate field " + field);
            }

            if (field.getAnnotation(JsonUnwrapped.class) != null) {
                objectContext = objectContext.forField(field);
                final PropertyType unwrappedFieldType = objectContext.getPropertyType();
                if (PropertyType.PRIMITIVE_TYPES.contains(unwrappedFieldType)) {
                    throw new IllegalStateException("can not unwrap primitive type "
                            + unwrappedFieldType);
                }
                fieldContextMap.putAll(getFieldContextMap(objectContext, context));
            } else {
                fieldContextMap.put(field, objectContext);
            }
        }
    }

    private int byName(Field field1, Field field2) {
        return field1.getName().compareTo(field2.getName());
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
        pathContext = pathContext.enter(builder.getName(),
                propertyType == PropertyType.OBJECT ? rawType : null);
        return builder.withPath(pathContext.getCurrentPath()).withChildren(
                getNestedProperties(objectContext, pathContext, context)).build();
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
