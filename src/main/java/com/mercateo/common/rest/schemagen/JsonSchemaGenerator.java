package com.mercateo.common.rest.schemagen;

import java.util.Optional;

import com.mercateo.common.rest.schemagen.link.ScopeMethod;
import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;

public interface JsonSchemaGenerator {
    /**
     * Creates the output schema for the given method applying a JsonView class
     * 
     * @param method
     *            the method for which the output schema should be generated
     * @param fieldCheckerForSchema
     *            checks for every field, if it should be included in the schema
     * @return Optional schema string
     */
    Optional<String> createInputSchema(ScopeMethod method,
            FieldCheckerForSchema fieldCheckerForSchema);

    /**
     * Creates the output schema for the given method
     *
     * @param method
     *            the method for which the output schema should be generated
     * @param fieldCheckerForSchema
     *            checks for every field, if it should be included in the schema
     * @return Optional schema string
     */
    Optional<String> createOutputSchema(ScopeMethod method,
            FieldCheckerForSchema fieldCheckerForSchema);
}