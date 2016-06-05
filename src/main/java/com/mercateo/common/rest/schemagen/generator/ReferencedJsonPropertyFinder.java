package com.mercateo.common.rest.schemagen.generator;

import com.mercateo.common.rest.schemagen.JsonProperty;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ReferencedJsonPropertyFinder {

    public Set<JsonProperty> getReferencedJsonProperties(JsonProperty jsonProperty) {
        return getReferencedJsonProperties(jsonProperty, new HashMap<>(), new HashSet<>());
    }

    private Set<JsonProperty> getReferencedJsonProperties(JsonProperty jsonProperty, Map<String, JsonProperty> pathMap, Set<JsonProperty> referencedJsonProperties) {
        pathMap.put(jsonProperty.getPath(), jsonProperty);

        if (jsonProperty.getRef() != null) {
            final JsonProperty referencedJsonProperty = pathMap.get(jsonProperty.getRef());
            if (referencedJsonProperty == null) {
                throw new IllegalStateException("There is an reference id (+" + jsonProperty.getRef()
                        + "), but no referenced object for it");
            }
            referencedJsonProperties.add(referencedJsonProperty);
        }

        for (JsonProperty childJsonProperty : jsonProperty.getProperties()) {
            getReferencedJsonProperties(childJsonProperty, pathMap, referencedJsonProperties);
        }
        return referencedJsonProperties;
    }
}
