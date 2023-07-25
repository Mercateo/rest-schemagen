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

import static com.mercateo.common.rest.schemagen.util.OptionalUtil.collect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.mercateo.common.rest.schemagen.JsonHyperSchema;
import com.mercateo.common.rest.schemagen.JsonHyperSchemaCreator;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Link;

public class HyperSchemaCreator {

    private final ObjectWithSchemaCreator objectWithSchemaCreator;

    private final JsonHyperSchemaCreator jsonHyperSchemaCreator;

    @Inject
    public HyperSchemaCreator(ObjectWithSchemaCreator objectWithSchemaCreator, JsonHyperSchemaCreator jsonHyperSchemaCreator) {
        this.objectWithSchemaCreator = objectWithSchemaCreator;
        this.jsonHyperSchemaCreator = jsonHyperSchemaCreator;
    }

    @SafeVarargs
    public final <T> ObjectWithSchema<T> create(T object, Optional<Link>... links) {
        JsonHyperSchema hyperSchema = jsonHyperSchemaCreator.from(collect(links));
        return objectWithSchemaCreator.create(object, hyperSchema);
    }

    @SafeVarargs
    public final <T> ObjectWithSchema<T> create(T object, List<Link>... linkArray) {
        ArrayList<Link> links = new ArrayList<>();
        Arrays.stream(linkArray).forEach(links::addAll);

        JsonHyperSchema hyperSchema = jsonHyperSchemaCreator.from(links);
        return objectWithSchemaCreator.create(object, hyperSchema);
    }
}
