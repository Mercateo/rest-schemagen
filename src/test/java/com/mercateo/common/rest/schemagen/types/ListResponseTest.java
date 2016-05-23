package com.mercateo.common.rest.schemagen.types;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.core.Link;

import org.junit.Test;

import com.mercateo.common.rest.schemagen.JsonHyperSchema;

public class ListResponseTest {

    @Test
    public void testListResponseBuilder() {

        Link containerLink = Link.fromPath( "/").build();
        final ListResponse<String> listResponse = ListResponse.<Integer, String> builder()
                .withList(Arrays.asList(1, 2, 3)).withElementMapper(this::elementMapper)
                .withContainerLinks(containerLink).build();

        final List<String> strings = listResponse.object.members.stream().map(o -> o.object)
                .collect(Collectors.toList());
        assertThat(strings).containsExactly("1", "2", "3");

        final List<String> links = listResponse.object.members.stream().map(
                o -> o.schema.getLinks().iterator().next().getUri().toString()).collect(
                Collectors.toList());
        assertThat(links).containsExactly("/1", "/2", "/3");

        assertThat(listResponse.schema.getLinks().iterator().next().getUri().toString()).isEqualTo(
                "/");
    }

    private ObjectWithSchema<String> elementMapper(Integer number) {
        final Link link = Link.fromPath("/" + number).build();
        return ObjectWithSchema.create(Integer.toHexString(number), JsonHyperSchema.from(link));
    }

}