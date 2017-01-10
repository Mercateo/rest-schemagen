package com.mercateo.common.rest.schemagen;


import org.junit.Test;

import javax.ws.rs.core.Link;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class JsonHyperSchemaCreatorTest {
    @Test
    public void name() throws Exception {
        List<Link> links = Collections.singletonList(mock(Link.class));

        JsonHyperSchema jsonHyperSchema = new JsonHyperSchemaCreator().from(links);

        assertThat(jsonHyperSchema.getLinks()).isEqualTo(links);
    }
}