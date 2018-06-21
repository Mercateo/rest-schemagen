/**
 * Copyright © 2015 Mercateo AG (http://www.mercateo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
