package com.mercateo.common.rest.schemagen.json.mapper;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mercateo.common.rest.schemagen.generator.JsonPropertyResult;

public class PropertyJsonSchemaMapper {

    public PropertyJsonSchemaMapper() {
    }

    /**
     * Convert the property hierarchy given in {@code property} to a JSON
     * string.
     * 
     * @param jsonProperty
     *            property hierarchy to be converted
     * @return JSONObject representing the schema of the given property
     *         hierarchy
     */
    public ObjectNode toJson(JsonPropertyResult jsonProperty) {
        return new PropertyJsonSchemaMapperForRoot(jsonProperty).toJson(jsonProperty.getRoot());
    }
}
