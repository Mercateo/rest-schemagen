/*
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

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mercateo.common.rest.schemagen.JsonHyperSchema;
import com.mercateo.common.rest.schemagen.ListSlicer;
import com.mercateo.common.rest.schemagen.PaginationLinkBuilder;
import com.mercateo.common.rest.schemagen.PaginationLinkBuilder.PaginationLinkCreator;

import jakarta.ws.rs.core.Link;

public class PaginatedResponseBuilder<ElementIn, ElementOut> extends
        ResponseBuilderAbstract<PaginatedResponseBuilder<ElementIn, ElementOut>, ElementIn, ElementOut, PaginatedResponse<ElementOut>> {

    private PaginationLinkCreator paginationLinkCreator;

    private PaginatedList<ElementIn> paginatedList;

    /**
     * @deprecated please use {@link PaginatedResponse#builder()} instead
     */
    @Deprecated
    public PaginatedResponseBuilder() {
    }

    @Override
    public PaginatedResponse<ElementOut> build() {
        requireNonNull(paginatedList);
        requireNonNull(elementMapper);
        requireNonNull(containerLinks);
        requireNonNull(paginationLinkCreator);

        PaginatedList<ObjectWithSchema<ElementOut>> mappedList = new PaginatedList<>(
                paginatedList.total, paginatedList.offset, paginatedList.limit,
                paginatedList.members.stream().map(elementMapper).collect(Collectors.toList()));

        final List<Link> containerLinks = new ArrayList<>(this.containerLinks);

        containerLinks.addAll(PaginationLinkBuilder.of(paginatedList.total, paginatedList.offset,
                paginatedList.limit).generateLinks(paginationLinkCreator));

        JsonHyperSchema schema = JsonHyperSchema.from(containerLinks);
        return PaginatedResponse.create(mappedList.members, mappedList.total, mappedList.offset,
                mappedList.limit, schema);
    }

    public PaginatedResponseBuilder<ElementIn, ElementOut> withPaginationLinkCreator(
            PaginationLinkCreator paginationLinkCreator) {
        this.paginationLinkCreator = requireNonNull(paginationLinkCreator);
        return this;
    }

    public PaginatedResponseBuilder<ElementIn, ElementOut> withList(List<ElementIn> list,
            Integer offset, Integer limit) {
        paginatedList = ListSlicer.withDefaultInterval().create(offset, limit).createSliceOf(list);
        return this;
    }

    public PaginatedResponseBuilder<ElementIn, ElementOut> withList(
            PaginatedList<ElementIn> paginatedList) {
        this.paginatedList = paginatedList;
        return this;
    }
}
