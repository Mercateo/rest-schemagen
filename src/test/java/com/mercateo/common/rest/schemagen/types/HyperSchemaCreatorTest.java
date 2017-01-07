package com.mercateo.common.rest.schemagen.types;

import com.mercateo.common.rest.schemagen.JsonHyperSchemaCreator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javax.inject.Inject;
import javax.ws.rs.core.Link;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class HyperSchemaCreatorTest {

    @Mock
    private Object object;

    @Spy
    private ObjectWithSchemaCreator objectWithSchemaCreator;

    @Spy
    private JsonHyperSchemaCreator jsonHyperSchemaCreator;

    @InjectMocks
    private HyperSchemaCreator hyperSchemaCreator;

    @Test
    public void shouldWrapPayload() {
        ObjectWithSchema<Object> objectWithSchema = hyperSchemaCreator.create(object, Optional.of(mock(Link.class)));

        assertThat(objectWithSchema.object).isEqualTo(object);
    }

    @Test
    public void shouldIgnoreNotExistentLinks() throws Exception {
        ObjectWithSchema<Object> objectWithSchema = hyperSchemaCreator.create(object, Optional.empty());

        assertThat(objectWithSchema.schema.getLinks()).isEmpty();
    }

    @Test
    public void shouldAccumulateOptionalLinks() throws Exception {
        Link link1 = mock(Link.class);
        Link link2 = mock(Link.class);

        ObjectWithSchema<Object> objectWithSchema = hyperSchemaCreator
                .create(object, Optional.of(link1), Optional.of(link2));

        assertThat(objectWithSchema.schema.getLinks()).containsExactly(link1, link2);
    }

    @Test
    public void shouldAccumulateLinkCollections() throws Exception {
        Link link1 = mock(Link.class);
        Link link2 = mock(Link.class);

        ObjectWithSchema<Object> objectWithSchema = hyperSchemaCreator
                .create(object, Collections.singletonList(link1), Collections.singletonList(link2));

        assertThat(objectWithSchema.schema.getLinks()).containsExactly(link1, link2);
    }
}