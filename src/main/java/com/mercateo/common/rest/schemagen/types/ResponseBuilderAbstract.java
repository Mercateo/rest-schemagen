package com.mercateo.common.rest.schemagen.types;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.core.Link;

public abstract class ResponseBuilderAbstract<Self extends ResponseBuilderAbstract<Self, ElementIn, ElementOut, Container>, ElementIn, ElementOut, Container> {

    protected Function<ElementIn, ObjectWithSchema<ElementOut>> elementMapper;

    protected List<Link> containerLinks = Collections.emptyList();

    public abstract Container build();

    @SuppressWarnings("unchecked")
    public Self withElementMapper(Function<ElementIn, ObjectWithSchema<ElementOut>> elementMapper) {
        this.elementMapper = requireNonNull(elementMapper);
        // noinspection unchecked
        return (Self) this;
    }

    /**
     * @deprecated please use {@link #withContainerLinks(Optional[])} instead
     *
     * @param containerLinks
     * @return
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public Self withContainerLinks(List<Optional<Link>> containerLinks) {
        this.containerLinks = createList(requireNonNull(containerLinks));

        // noinspection unchecked
        return (Self) this;
    }

    @SafeVarargs
    public final Self withContainerLinks(Optional<Link>... containerLinks) {
        this.containerLinks = createList(Arrays.asList(requireNonNull(containerLinks)));

        // noinspection unchecked
        return (Self) this;
    }

    public final Self withContainerLinks(Link ... containerLinks) {
        this.containerLinks = Arrays.asList(requireNonNull(containerLinks));

        // noinspection unchecked
        return (Self) this;
    }

    private List<Link> createList(Collection<Optional<Link>> optionals) {
        return optionals
                .stream()
                .flatMap(element -> element.map(Stream::of).orElse(Stream.empty()))
                .collect(Collectors.toList());
    }
}
