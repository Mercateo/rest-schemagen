package com.mercateo.common.rest.schemagen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.Link;

public class JsonHyperSchemaCreatorTest {
    @Test
    public void name() throws Exception {
        List<Link> links = Collections.singletonList(mock(Link.class));

        JsonHyperSchema jsonHyperSchema = new JsonHyperSchemaCreator().from(links);

        assertThat(jsonHyperSchema.getLinks()).isEqualTo(links);
    }
}
