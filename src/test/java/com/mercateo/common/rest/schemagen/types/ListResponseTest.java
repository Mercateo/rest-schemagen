package com.mercateo.common.rest.schemagen.types;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercateo.common.rest.schemagen.JsonHyperSchema;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.Link;

import org.junit.Test;

public class ListResponseTest {

    @Test
    public void testListResponseBuilder() {

        Link containerLink = Link.fromPath("/").build();
        final ListResponse<String> listResponse = ListResponse
            .<Integer, String> builder()
            .withList(Arrays.asList(1, 2, 3))
            .withElementMapper(this::elementMapper)
            .withContainerLinks(containerLink)
            .build();

        final List<String> strings = listResponse.getObject().members.stream().map(o -> o.getObject()).collect(Collectors
            .toList());
        assertThat(strings).containsExactly("1", "2", "3");

        final List<String> links = listResponse.getObject().members
            .stream()
            .map(o -> o.getSchema().getLinks().iterator().next().getUri().toString())
            .collect(Collectors.toList());
        assertThat(links).containsExactly("/1", "/2", "/3");

        assertThat(listResponse.getSchema().getLinks().iterator().next().getUri().toString()).isEqualTo("/");
    }

    private ObjectWithSchema<String> elementMapper(Integer number) {
        final Link link = Link.fromPath("/" + number).build();
        return ObjectWithSchema.create(Integer.toHexString(number), JsonHyperSchema.from(link));
    }

    static class Payload {
        public String value;
    }

    @Test
    public void shouldSerialize() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();

        final Payload payload = new Payload();
        payload.value = "foo";
        final ObjectWithSchema<Payload> element = ObjectWithSchema.create(payload, JsonHyperSchema.from(Collections
            .emptyList()));
        final ListResponse<Payload> listResponse = new ListResponse<>(Collections.singletonList(element),
                JsonHyperSchema.from(Collections.emptyList()));

        final String jsonString = objectMapper.writeValueAsString(listResponse);

        assertThat(jsonString).isEqualTo(
                "{\"members\":[{\"value\":\"foo\",\"_schema\":{\"links\":[]}}],\"_schema\":{\"links\":[]}}");
    }

    @Test
    public void shouldDeserialize() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.deactivateDefaultTyping();

        final String content = "{\"members\": [{\"value\": \"foo\", \"_schema\":{\"links\":[]}}, {\"value\": \"bar\", \"_schema\":{\"links\":[]}}], \"_schema\":{\"links\":[]}}";

        final ListResponse<Payload> listResponse = mapper.readValue(content,
                new TypeReference<ListResponse<Payload>>() {
                });

        assertThat(listResponse.getMembers())
            .extracting(ObjectWithSchema::getObject)
            .extracting(p -> p.value)
            .containsExactly("foo", "bar");
    }
}