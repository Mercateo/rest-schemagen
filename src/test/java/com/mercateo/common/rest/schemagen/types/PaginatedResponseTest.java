package com.mercateo.common.rest.schemagen.types;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.core.Link;

import com.mercateo.common.rest.schemagen.PaginationLinkBuilder;
import com.mercateo.common.rest.schemagen.link.relation.Rel;
import com.mercateo.common.rest.schemagen.link.relation.RelationContainer;
import org.junit.Test;

import com.mercateo.common.rest.schemagen.JsonHyperSchema;

public class PaginatedResponseTest {

    @Test
    public void testPaginatedResponseBuilder() {

        List<Optional<Link>> containerLinks = Collections.singletonList(Optional.of(Link.fromPath(
                "/").build()));
        final PaginatedResponse<String> listResponse = PaginatedResponse.<Integer, String> builder()
                .withList(Arrays.asList(1, 2, 3), 1, 2).withElementMapper(this::elementMapper)
                .withPaginationLinkCreator(this::paginationLinkCreator)
                .withContainerLinks(containerLinks).build();

        final List<String> strings = listResponse.object.members.stream().map(o -> o.object)
                .collect(Collectors.toList());
        assertThat(strings).containsExactly("2", "3");

        final List<String> links = listResponse.object.members.stream().map(
                o -> o.schema.getLinks().iterator().next().getUri().toString()).collect(
                Collectors.toList());
        assertThat(links).containsExactly("/2", "/3");

        assertThat(listResponse.schema.getLinks().iterator().next().getUri().toString()).isEqualTo(
                "/");
    }

    private ObjectWithSchema<String> elementMapper(Integer number) {
        final Link link = Link.fromPath("/" + number).build();
        return ObjectWithSchema.create(Integer.toHexString(number), JsonHyperSchema.from(link));
    }

    private Optional<Link> paginationLinkCreator(RelationContainer rel, int offset, int limit) {
        return Optional.of(Link.fromPath("/").param("offset", Integer.toString(offset)).param("limit", Integer.toString(limit)).build());
    }

}