package com.mercateo.common.rest.schemagen.link.injection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.net.URI;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
    private BaseUri baseUri;

    @Mock
    private MethodCheckerForLink methodCheckerForLink;

    @Mock
    private FieldCheckerForSchema fieldCheckerForSchema;

    @InjectMocks
    private LinkMetaFactoryFactory linkMetaFactoryFactory;

    private URI baseUriValue;

    @Test
    public void testProvideLinkMetaFactory() {
        baseUriValue = URI.create("/base");
        when(baseUri.get()).thenReturn(baseUriValue);
        final LinkMetaFactory linkMetaFactory = linkMetaFactoryFactory.provide();

        final LinkFactoryContext linkFactoryContext = linkMetaFactory.getFactoryContext();

        assertThat(linkFactoryContext.getSchemaGenerator()).isEqualTo(schemaGenerator);
        assertThat(linkFactoryContext.getBaseUri()).isEqualTo(baseUriValue);
        assertThat(linkFactoryContext.getMethodCheckerForLink()).isEqualTo(methodCheckerForLink);
        assertThat(linkFactoryContext.getFieldCheckerForSchema()).isEqualTo(fieldCheckerForSchema);
    }
}