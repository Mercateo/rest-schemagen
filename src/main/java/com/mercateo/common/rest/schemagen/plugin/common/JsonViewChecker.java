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
package com.mercateo.common.rest.schemagen.plugin.common;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonView;
import com.mercateo.common.rest.schemagen.parameter.CallContext;
import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;

public class JsonViewChecker implements FieldCheckerForSchema {

    @Override
    public boolean test(Field field, CallContext context) {
        checkNotNull(field);
        checkNotNull(context);
        final JsonView jsonView = field.getAnnotation(JsonView.class);
        if (jsonView != null) {
            @SuppressWarnings("rawtypes")
            Optional<Set<Class>> viewClasses = context.getAdditionalObjectsFor(Class.class);
            return !viewClasses.isPresent() || Arrays.stream(jsonView.value()).anyMatch(c->viewClasses.get().contains(c));
        }
        return true;
    }

}
