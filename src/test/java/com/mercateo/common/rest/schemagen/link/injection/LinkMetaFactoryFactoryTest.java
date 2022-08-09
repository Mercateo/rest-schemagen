package com.mercateo.common.rest.schemagen.link.injection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.net.URI;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.mercateo.common.rest.schemagen.JsonSchemaGenerator;
import com.mercateo.common.rest.schemagen.link.LinkFactoryContext;
import com.mercateo.common.rest.schemagen.link.LinkMetaFactory;
import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;
import com.mercateo.common.rest.schemagen.plugin.MethodCheckerForLink;

@RunWith(MockitoJUnitRunner.class)
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

        verifyZeroInteractions(schemaGenerator, linkFactoryContext, linkMetaFactory);
    }
}