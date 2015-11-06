package com.mercateo.common.rest.schemagen.types;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;

import com.mercateo.common.rest.schemagen.JsonHyperSchema;

public class ListResponseBuilder<ElementIn, ElementOut> extends
        ResponseBuilderAbstract<ListResponseBuilder<ElementIn, ElementOut>, ElementIn, ElementOut, ListResponse<ElementOut>> {
    private List<ElementIn> list;

    /**
     * @deprecated please use {@link ListResponse#builder()} instead
     */
    @Deprecated
    public ListResponseBuilder() {
    }

    @Override
    public ListResponse<ElementOut> build() {
        requireNonNull(list);
        requireNonNull(elementMapper);
        requireNonNull(containerLinks);

        List<ObjectWithSchema<ElementOut>> mappedList = list.stream().map(elementMapper).collect(
                Collectors.toList());

        JsonHyperSchema schema = JsonHyperSchema.fromOptional(containerLinks);
        return ListResponse.create(mappedList, schema);

    }

    public ListResponseBuilder<ElementIn, ElementOut> withList(List<ElementIn> list) {
        this.list = list;
        return this;
    }
}
