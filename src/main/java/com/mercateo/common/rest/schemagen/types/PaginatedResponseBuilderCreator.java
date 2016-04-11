package com.mercateo.common.rest.schemagen.types;

public class PaginatedResponseBuilderCreator<ElementIn, ElementOut> {
    public PaginatedResponseBuilder<ElementIn, ElementOut> builder() {
        return PaginatedResponse.<ElementIn, ElementOut> builder();
    }
}
