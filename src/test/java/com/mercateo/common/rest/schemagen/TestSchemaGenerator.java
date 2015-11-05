package com.mercateo.common.rest.schemagen;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mercateo.common.rest.schemagen.plugin.IndividualSchemaGenerator;

public class TestSchemaGenerator implements IndividualSchemaGenerator {

    @Override
    public ObjectNode create() {
        final ObjectNode result = new ObjectNode(new JsonNodeFactory(true));
        result.put("type", "test");
        return result;
    }
}
