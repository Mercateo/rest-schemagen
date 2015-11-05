package com.mercateo.common.rest.schemagen.plugin;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * This class could provide a individual json schemas for fields. Use with
 * {@link PropertySchema}
 * 
 */
public interface IndividualSchemaGenerator {
    ObjectNode create();
}
