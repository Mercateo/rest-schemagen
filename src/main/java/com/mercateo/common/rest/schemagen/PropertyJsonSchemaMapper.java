package com.mercateo.common.rest.schemagen;

import java.util.List;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.mercateo.common.rest.schemagen.plugin.IndividualSchemaGenerator;

public class PropertyJsonSchemaMapper {

    public static final JsonNodeFactory nodeFactory = new JsonNodeFactory(true);

    /**
     * Convert the property hierarchy given in {@code property} to a JSON
     * string.
     * 
     * @param property
     *            property hierarchy to be converted
     * @return JSONObject representing the schema of the given property
     *         hierarchy
     */
    public ObjectNode toJson(Property property) {
        return createPropertyEntry(property, createObjectNode());
    }

    public ObjectNode createObjectNode() {
        return new ObjectNode(nodeFactory);
    }

    private ObjectNode createPropertyEntry(Property property) {
        return createPropertyEntry(property, createObjectNode());
    }

    private ObjectNode createPropertyEntry(Property property, final ObjectNode result) {

        final Class<? extends IndividualSchemaGenerator> generator = property.getGenerator();

        if (generator == null) {
            if (property.getRef() != null) {
                result.put("$ref", property.getRef());

            } else {
                if (property.getId() != null) {
                    result.put("id", property.getId());
                }
                switch (property.getType()) {
                case OBJECT:
                    result.put("type", "object");
                    result.set("properties", createProperties(property.getProperties()));
                    final ArrayNode requiredElements = createRequiredElementsArray(property
                            .getProperties());
                    if (requiredElements.size() > 0) {
                        result.set("required", requiredElements);
                    }
                    break;

                case ARRAY:
                    result.put("type", "array");
                    result.set("items", createPropertyEntry(property.getProperties().get(0)));
                    break;

                case STRING:
                    result.put("type", "string");
                    if (hasAllowedValues(property)) {
                        result.set("enum", getAllowedValues(property));
                    }
                    if (hasDefaultValue(property)) {
                        result.set("default", getDefaultValue(property));
                    }
                    property.getSizeConstraints().getMin().ifPresent(x -> result.put("minLength", x));
                    property.getSizeConstraints().getMax().ifPresent(x -> result.put("maxLength", x));
                    break;

                case INTEGER:
                    result.put("type", "integer");
                    property.getValueConstraints().getMin().ifPresent(x -> result.put("minimum", x));
                    property.getValueConstraints().getMax().ifPresent(x -> result.put("maximum", x));
                    break;
                case NUMBER:
                    result.put("type", "number");
                    break;

                case BOOLEAN:
                    result.put("type", "boolean");
                    result.put("default", false);
                    if (hasAllowedValues(property)) {
                        result.set("enum", getAllowedValues(property));
                    }
                    break;

                default:

                    break;
                }
            }
            return result;
        } else {
            final IndividualSchemaGenerator individualSchemaGenerator;

            try {
                individualSchemaGenerator = generator.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            return individualSchemaGenerator.create();
        }
    }

    private boolean hasAllowedValues(Property property) {
        return !property.getAllowedValues().isEmpty();
    }

    private ArrayNode getAllowedValues(Property property) {
        final ArrayNode arrayNode = new ArrayNode(nodeFactory);
        final List<String> allowedValues = property.getAllowedValues();
        for (String allowedValue : allowedValues) {
            arrayNode.add(new TextNode(allowedValue));
        }
        return arrayNode;
    }

    private boolean hasDefaultValue(Property property) {
        final String defaultValue = property.getDefaultValue();
        return defaultValue != null && !defaultValue.isEmpty();
    }

    private TextNode getDefaultValue(Property property) {
        return new TextNode(property.getDefaultValue());
    }

    private ObjectNode createProperties(List<Property> properties) {
        final ObjectNode result = createObjectNode();
        for (Property property : properties) {
            result.set(property.getName(), createPropertyEntry(property));
        }
        return result;
    }

    private ArrayNode createRequiredElementsArray(List<Property> properties) {
        final ArrayNode result = new ArrayNode(nodeFactory);
        for (Property property : properties) {
            if (property.isRequired()) {
                result.add(property.getName());
            }
        }
        return result;
    }

}
