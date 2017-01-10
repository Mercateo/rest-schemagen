package com.mercateo.common.rest.schemagen.link.injection;

import com.mercateo.common.rest.schemagen.link.LinkFactoryContext;
import com.mercateo.common.rest.schemagen.link.helper.BaseUriCreator;
import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;
import com.mercateo.common.rest.schemagen.plugin.MethodCheckerForLink;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LinkFactoryContextFactoryTest {

    @Mock
    private UriInfo uriInfo;

    @Mock
    private HttpHeaders httpHeaders;

    @Mock
    private BaseUriCreator baseUriCreator;

    @Mock
    private MethodCheckerForLink methodCheckerForLink;

    @Mock
    private FieldCheckerForSchema fieldCheckerForSchema;

    @InjectMocks
    private LinkFactoryContextFactory factory;

    @Mock
    private MultivaluedMap<String, String> requestHeaders;

    @Test
    public void shouldProvideLinkFactoryContext() throws Exception {
        URI defaultBaseUri = new URI("http://host/path");
        URI baseUri = new URI("http://server/path");
        when(uriInfo.getBaseUri()).thenReturn(defaultBaseUri);
        when(httpHeaders.getRequestHeaders()).thenReturn(requestHeaders);
        when(baseUriCreator.createBaseUri(defaultBaseUri, requestHeaders)).thenReturn(baseUri);

        final LinkFactoryContext linkFactoryContext = factory.provide();

        assertThat(linkFactoryContext.getBaseUri()).isEqualTo(baseUri);
        assertThat(linkFactoryContext.getMethodCheckerForLink()).isEqualTo(methodCheckerForLink);
        assertThat(linkFactoryContext.getFieldCheckerForSchema()).isEqualTo(fieldCheckerForSchema);
    }
}