package com.mercateo.common.rest.schemagen;

import java.util.Optional;

import com.mercateo.common.rest.schemagen.link.Scope;
import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;

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
     * @param scope
     *            the method scope for which the output schema should be generated
     * @param fieldCheckerForSchema
     *            checks for every field, if it should be included in the schema
     * @return Optional schema string
     */
    Optional<String> createOutputSchema(Scope scope,
            FieldCheckerForSchema fieldCheckerForSchema);
}