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
package com.mercateo.common.rest.schemagen;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Field;

import com.mercateo.common.rest.schemagen.parameter.CallContext;
import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;

public class SchemaPropertyContext {

    private final CallContext callContext;

    private final FieldCheckerForSchema fieldCheckerForSchema;

    public SchemaPropertyContext(CallContext callContext,
            FieldCheckerForSchema fieldCheckerForSchema) {
        this.callContext = checkNotNull(callContext);
        this.fieldCheckerForSchema = checkNotNull(fieldCheckerForSchema);
    }

    public boolean isFieldApplicable(Field field) {
        checkNotNull(field);
        return fieldCheckerForSchema.test(field, callContext);
    }
}
