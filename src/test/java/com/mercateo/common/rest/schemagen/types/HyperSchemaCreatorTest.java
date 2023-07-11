package com.mercateo.common.rest.schemagen.types;

import com.mercateo.common.rest.schemagen.JsonHyperSchemaCreator;
import jakarta.ws.rs.core.Link;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
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

        assertThat(objectWithSchema.getObject()).isEqualTo(object);
    }

    @Test
    public void shouldIgnoreNotExistentLinks() throws Exception {
        ObjectWithSchema<Object> objectWithSchema = hyperSchemaCreator.create(object, Optional.empty());

        assertThat(objectWithSchema.getSchema().getLinks()).isEmpty();
    }

    @Test
    public void shouldAccumulateOptionalLinks() throws Exception {
        Link link1 = mock(Link.class);
        Link link2 = mock(Link.class);

        ObjectWithSchema<Object> objectWithSchema = hyperSchemaCreator
                .create(object, Optional.of(link1), Optional.of(link2));

        assertThat(objectWithSchema.getSchema().getLinks()).containsExactly(link1, link2);
    }

    @Test
    public void shouldAccumulateLinkCollections() throws Exception {
        Link link1 = mock(Link.class);
        Link link2 = mock(Link.class);

        ObjectWithSchema<Object> objectWithSchema = hyperSchemaCreator
                .create(object, Collections.singletonList(link1), Collections.singletonList(link2));

        assertThat(objectWithSchema.getSchema().getLinks()).containsExactly(link1, link2);
    }
}
