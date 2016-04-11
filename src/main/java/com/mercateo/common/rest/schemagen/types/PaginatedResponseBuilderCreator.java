package com.mercateo.common.rest.schemagen.types;

public class PaginatedResponseBuilderCreator {
    public <ElementIn, ElementOut> PaginatedResponseBuilder<ElementIn, ElementOut> builder() {
        return PaginatedResponse.<ElementIn, ElementOut> builder();
    }
}
