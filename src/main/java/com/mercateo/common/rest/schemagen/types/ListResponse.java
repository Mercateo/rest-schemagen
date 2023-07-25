/*
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
package com.mercateo.common.rest.schemagen.types;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mercateo.common.rest.schemagen.JsonHyperSchema;

@JsonIgnoreProperties("object")
public class ListResponse<T> extends ObjectWithSchema<WrappedList<ObjectWithSchema<T>>> {

    @JsonCreator
    protected ListResponse(@JsonProperty("members") List<ObjectWithSchema<T>> members,
            @JsonProperty("_schema") JsonHyperSchema schema) {
        super(new WrappedList<>(members), schema, null);
    }

    @Override
    public String toString() {
        return "ListResponseRto [ payload=" + getObject().members + ", _schema=" + getSchema() + "]";
    }

    public static <T> ListResponse<T> create(List<ObjectWithSchema<T>> members, JsonHyperSchema schema) {
        return new ListResponse<>(members, schema);
    }

    public static <ElementIn, ElementOut> ListResponseBuilder<ElementIn, ElementOut> builder() {
        // noinspection deprecation
        return new ListResponseBuilder<>();
    }

    @JsonProperty("members")
    public List<ObjectWithSchema<T>> getMembers() {
        return getObject().members;
    }
}
