package com.mercateo.common.rest.schemagen;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mercateo.common.rest.schemagen.link.relation.Rel;

import jakarta.ws.rs.core.Link;

public class JsonHyperSchemaTest {

    private Link link1 = Link.fromPath("/value/1").rel("self").build();

    private Link link2 = Link.fromPath("/value").type("asdf").rel("create").build();

    private JsonHyperSchema hyperSchema;

    @BeforeEach
    public void setUp() {
        hyperSchema = JsonHyperSchema.from(null, link1, null, link2, null);
    }

    @Test
    public void shouldFindLinkByRel() {
        assertLink2();
    }

    @Test
    public void getLinkShouldReturnAllNonNullLinks() {
        assertThat(hyperSchema.getLinks()).containsExactly(link1, link2);
    }

    @Test
    public void shouldReturnEmptyResultWhenLinkByRelIsNotFound() {
        assertThat(hyperSchema.getByRel(Rel.DELETE)).isNotPresent();
    }

    @Test
    public void createFromOptionalLinks() {
        hyperSchema = JsonHyperSchema.from(Optional.empty(), Optional.of(link2));

        assertLink2();
    }

    @Test
    public void createFromCollection() {
        hyperSchema = JsonHyperSchema.fromOptional(Arrays.asList(Optional.empty(), Optional.of(link2)));

        assertLink2();
    }

    @Test
    public void createFromOptionalCollection() {
        hyperSchema = JsonHyperSchema.fromOptional(Arrays.asList(Optional.empty(), Optional.of(link2)));

        assertLink2();
    }

    private void assertLink2() {
        assertThat(hyperSchema.getByRel(Rel.CREATE))
                .isPresent()
                .hasValueSatisfying(v -> assertThat(v).isEqualTo(link2));
    }
}
