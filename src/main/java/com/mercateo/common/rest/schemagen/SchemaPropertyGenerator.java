package com.mercateo.common.rest.schemagen;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.collect.ImmutableMap;
import com.mercateo.common.rest.schemagen.generator.ImmutableJsonPropertyResult;
import com.mercateo.common.rest.schemagen.generator.JsonPropertyResult;
import com.mercateo.common.rest.schemagen.generator.ObjectContext;
import com.mercateo.common.rest.schemagen.generator.ObjectContextBuilder;
import com.mercateo.common.rest.schemagen.generator.PathContext;
import com.mercateo.common.rest.schemagen.generator.ReferencedJsonPropertyFinder;
import com.mercateo.common.rest.schemagen.generictype.GenericType;
import com.mercateo.common.rest.schemagen.util.EnumUtil;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SchemaPropertyGenerator {

    private static final Map<Class<?>, JsonProperty> builtins = ImmutableMap.of( //
            Date.class, JsonProperty.builderFor(Integer.class).withName("n/a").withIsRequired(true).build(),
            URL.class, JsonProperty.builderFor(String.class).withName("n/a").build());

    private final ReferencedJsonPropertyFinder referencedJsonPropertyFinder;

    public SchemaPropertyGenerator() {
        referencedJsonPropertyFinder = new ReferencedJsonPropertyFinder();
    }

    /**
     * Generate and return a property hierarchy for the object defined by the
     * given
     * {@link com.mercateo.common.rest.schemagen.generator.ObjectContextBuilder}
     * {@code objectContextBuilder}
     *
     * @param objectContextBuilder the object context builder for the object to build the
     *                             property hierarchy for
     * @param context              the current schema property context
     * @return property hierarchy
     */
    public JsonPropertyResult generateSchemaProperty(ObjectContextBuilder<?> objectContextBuilder,
                                               SchemaPropertyContext context) {
        return generateSchemaProperty(objectContextBuilder.build(), context);
    }

    /**
     * Generate and return a property hierarchy for the object defined by the
     * given {@link ObjectContext} {@code objectContext}
     *
     * @param objectContext the object context for the object to build the property
     *                      hierarchy for
     * @param context       the current schema property context
     * @return property hierarchy
     */
    public JsonPropertyResult generateSchemaProperty(ObjectContext<?> objectContext,
                                                     SchemaPropertyContext context) {
        JsonProperty rootJsonProperty = determineProperty("#", objectContext, new PathContext(), context);
        rootJsonProperty = updateName(rootJsonProperty, objectContext.getRawType().getSimpleName());
        return ImmutableJsonPropertyResult.of(
                rootJsonProperty,
                referencedJsonPropertyFinder.getReferencedJsonProperties(rootJsonProperty));
    }

    private JsonProperty updateName(JsonProperty jsonProperty, String name) {
        return JsonProperty.builderFrom(jsonProperty).withName(name).build();
    }

    private List<JsonProperty> getProperties(final ObjectContext<?> objectContext,
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
            for (Field field : objectContext.getGenericType().getDeclaredFields()) {
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
        final Type unwrappedType = objectContext.getType();
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

    private JsonProperty determineProperty(String name, ObjectContext<?> objectContext,
                                           PathContext pathContext, SchemaPropertyContext context) {
        if (builtins.containsKey(objectContext.getRawType())) {
            JsonProperty jsonPropertyTemplate = builtins.get(objectContext.getRawType());
            return JsonProperty.builderFrom(jsonPropertyTemplate).withName(name).build();
        } else {
            return determineObjectProperty(name, objectContext, pathContext, context);
        }
    }

    private JsonProperty determineObjectProperty(String name, ObjectContext<?> objectContext,
                                                 PathContext pathContext, SchemaPropertyContext context) {

        JsonProperty.Builder builder = JsonProperty.builderFor(objectContext).withName(name);

        final Type type = objectContext.getType();

        if (pathContext.isKnown(type)) {
            return builder.withPath(pathContext.getCurrentPath() + "/" + name)
                    .withRef(pathContext.getPath(type)).build();
        }

        final PropertyType propertyType = objectContext.getPropertyType();
        pathContext = pathContext.enter(name,
                propertyType == PropertyType.OBJECT ? type : null);
        return builder.withPath(pathContext.getCurrentPath()).withProperties(
                getNestedProperties(objectContext, pathContext, context)).build();
    }

    private List<JsonProperty> getNestedProperties(ObjectContext<?> objectContext,
                                                   PathContext pathContext, SchemaPropertyContext context) {
        List<JsonProperty> properties = new ArrayList<>();
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

    private List<JsonProperty> getDictProperties(ObjectContext<?> objectContext, PathContext pathContext, SchemaPropertyContext context) {
        ParameterizedType parameterizedType = (ParameterizedType) objectContext.getType();

        final Class<?> keyType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
        final Type valueType = parameterizedType.getActualTypeArguments()[1];
        final ObjectContext<?> valueObjectContext = ObjectContext.buildFor(GenericType.of(valueType)).build();
        JsonProperty valueJsonProperty = determineProperty("", valueObjectContext, pathContext, context);

        return Stream.of(keyType.getEnumConstants())
                .map(enumConstant -> {
                    final String enumName = EnumUtil.convertToString((Enum<?>) enumConstant);
                    return updateName(JsonProperty.builderFor(objectContext).from(valueJsonProperty).build(), enumName);
                }).collect(Collectors.toList());
    }

}
