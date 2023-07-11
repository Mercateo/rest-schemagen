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
package com.mercateo.common.rest.schemagen;

import com.mercateo.common.rest.schemagen.link.Scope;
import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;
import com.mercateo.common.rest.schemagen.plugin.TargetSchemaEnablerForLink;

import java.util.Optional;

public interface JsonSchemaGenerator {
    /**
     * Creates the output schema for the given method applying a JsonView class
     *
     * @param scope
     *            the method scope for which the output schema should be generated
     * @param fieldCheckerForSchema
     *            checks for every field, if it should be included in the schema
     * @return Optional schema string
     */
    Optional<String> createInputSchema(Scope scope,
            FieldCheckerForSchema fieldCheckerForSchema);

    /**
     * Creates the output schema for the given method
     *
     * @param scope                 the method scope for which the output schema should be generated
     * @param fieldCheckerForSchema checks for every field, if it should be included in the schema
     * @param targetSchemaEnablerForLink checks if the targetSchema should be created
     * @return Optional schema string
     */
    Optional<String> createOutputSchema(Scope scope, FieldCheckerForSchema fieldCheckerForSchema,
            TargetSchemaEnablerForLink targetSchemaEnablerForLink);
}
