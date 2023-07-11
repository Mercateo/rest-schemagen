/**
 * Copyright © 2015 Mercateo AG (http://www.mercateo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mercateo.common.rest.schemagen.types;

import jakarta.ws.rs.core.Link;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

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
