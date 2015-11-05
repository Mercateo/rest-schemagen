package com.mercateo.common.rest.schemagen.types;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.ws.rs.core.Link;

public abstract class ResponseBuilderAbstract<Self extends ResponseBuilderAbstract<Self, ElementIn, ElementOut, Container>, ElementIn, ElementOut, Container> {

    protected Function<ElementIn, ObjectWithSchema<ElementOut>> elementMapper;

    protected List<Optional<Link>> containerLinks;

    public abstract Container build();

    @SuppressWarnings("unchecked")
    public Self withElementMapper(Function<ElementIn, ObjectWithSchema<ElementOut>> elementMapper) {
        this.elementMapper = requireNonNull(elementMapper);
        // noinspection unchecked
        return (Self) this;
    }

    @SuppressWarnings("unchecked")
    public Self withContainerLinks(List<Optional<Link>> containerLinks) {
        this.containerLinks = new ArrayList<>(requireNonNull(containerLinks));
        // noinspection unchecked
        return (Self) this;

    }
}
