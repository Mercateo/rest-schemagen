package com.mercateo.common.rest.schemagen;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
import com.mercateo.common.rest.schemagen.generictype.GenericType;
import com.mercateo.common.rest.schemagen.util.EnumUtil;

public class SchemaPropertyGenerator {

    private static final Map<Class<?>, PropertyBuilder> builtins = ImmutableMap.of( //
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
     *            the current schema property context
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
     *            the current schema property context
     * @return property hierarchy
     */
    public Property generateSchemaProperty(ObjectContext<?> objectContext,
            SchemaPropertyContext context) {
        final Property property = determineProperty("#", objectContext, new PathContext(), context);
        addIdsToReferencedElements(property, new HashMap<>());
        final String name = objectContext.getRawType().getSimpleName();
        return updateName(objectContext, property, name);
    }

    private Property updateName(ObjectContext<?> objectContext, Property property, String name) {
        return Property.builderFor(objectContext).withProperty(property).withName(
                name).build();
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
                        throw new IllegalStateException("field name <" + name
                                + "> collision in class "
                                + objectContext.getRawType().getSimpleName());
                    }
                    return determineProperty(field.getName(), fieldContextMap.get(field).forField(
                            field), pathContext, context);
                }).collect(Collectors.toList());
    }

    private Map<Field, ObjectContext> getFieldContextMap(ObjectContext objectContext,
            SchemaPropertyContext context) {
        return getFieldContextMap(objectContext, new HashMap<>(), new HashSet<>(), context);
    }

    private Map<Field, ObjectContext> getFieldContextMap(ObjectContext objectContext,
            final Map<Field, ObjectContext> fieldContextMap, final Set<Type> unwrappedTypes,
            SchemaPropertyContext context) {
        do {
            for (Field field : objectContext.getType().getDeclaredFields()) {
                addFieldToMap(field, objectContext, fieldContextMap, unwrappedTypes, context);
            }
        } while ((objectContext = objectContext.forSuperType()) != null);
        return fieldContextMap;
    }

    private void addFieldToMap(Field field, ObjectContext objectContext,
            Map<Field, ObjectContext> fieldContextMap, Set<Type> unwrappedTypes,
            SchemaPropertyContext context) {
        if (objectContext.isApplicable(field, context)) {
            if (field.getAnnotation(JsonUnwrapped.class) != null) {
                fieldContextMap.putAll(getUnwrappedFieldsMap(field, objectContext, unwrappedTypes,
                        context));
            } else {
                fieldContextMap.put(field, objectContext);
            }
        }
    }

    private Map<Field, ObjectContext> getUnwrappedFieldsMap(Field field,
            ObjectContext objectContext, Set<Type> unwrappedTypes, SchemaPropertyContext context) {
        objectContext = objectContext.forField(field);
        final Type unwrappedType = objectContext.getType().getType();
        if (unwrappedTypes.contains(unwrappedType)) {
            throw new IllegalStateException(String.format(
                    "recursion detected while unwrapping field <%s> in <%s>", field.getName(),
                    unwrappedType.getTypeName()));
        }
        unwrappedTypes.add(unwrappedType);
        final PropertyType unwrappedFieldType = objectContext.getPropertyType();
        if (PropertyType.PRIMITIVE_TYPES.contains(unwrappedFieldType)) {
            throw new IllegalStateException("can not unwrap primitive type " + unwrappedFieldType);
        }
        return getFieldContextMap(objectContext, new HashMap<>(), unwrappedTypes, context);
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
                final PropertySubType subType = objectContext.getPropertySubType();
                switch (subType) {
                    case DICT:
                        properties = getDictProperties(objectContext, pathContext, context);
                        break;
                    default:
                        properties = getProperties(objectContext, pathContext, context);
                }
                break;

            case ARRAY:
                properties
                        .add(determineProperty("", objectContext.getContained(), pathContext, context));
                break;

            default:
                break;
        }
        return properties;
    }

    private List<Property> getDictProperties(ObjectContext<?> objectContext, PathContext pathContext, SchemaPropertyContext context) {
        ParameterizedType parameterizedType = (ParameterizedType) objectContext.getType().getType();

        final Class<?> keyType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
        final Type valueType = parameterizedType.getActualTypeArguments()[1];
        final ObjectContext<?> valueObjectContext = ObjectContext.buildFor(GenericType.of(valueType)).build();
        Property valueProperty = determineProperty("", valueObjectContext, pathContext, context);

        return Stream.of(keyType.getEnumConstants())
                .map(enumConstant -> {
                    final String enumName = EnumUtil.convertToString((Enum<?>) enumConstant);
                    return updateName(objectContext, valueProperty, enumName);
                }).collect(Collectors.toList());
    }

}
