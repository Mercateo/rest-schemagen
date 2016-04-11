package com.mercateo.common.rest.schemagen.types;

public class ListResponseBuilderCreator<ElementIn, ElementOut> {
    public ListResponseBuilder<ElementIn, ElementOut> builder() {
        return ListResponse.<ElementIn, ElementOut> builder();
    }
}
