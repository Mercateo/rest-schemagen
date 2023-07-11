package com.mercateo.common.rest.schemagen.link.injection;

import com.mercateo.common.rest.schemagen.JsonSchemaGenerator;
import com.mercateo.common.rest.schemagen.link.LinkFactoryContext;
import com.mercateo.common.rest.schemagen.link.LinkMetaFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class LinkMetaFactoryFactoryTest {

    @Mock
    private JsonSchemaGenerator schemaGenerator;

    @Mock
    private LinkFactoryContext linkFactoryContext;

    @InjectMocks
    private LinkMetaFactoryFactory linkMetaFactoryFactory;

    @Test
    public void testProvideLinkMetaFactory() {
        final LinkMetaFactory linkMetaFactory = linkMetaFactoryFactory.provide();

        assertThat(linkMetaFactory.getSchemaGenerator()).isEqualTo(schemaGenerator);
        assertThat(linkMetaFactory.getFactoryContext()).isEqualTo(linkFactoryContext);
    }

    @Test
    public void disposeShouldDoNothing() throws Exception {
        final LinkMetaFactory linkMetaFactory = mock(LinkMetaFactory.class);
        linkMetaFactoryFactory.dispose(linkMetaFactory);

        verifyNoInteractions(schemaGenerator, linkFactoryContext, linkMetaFactory);
    }
}
