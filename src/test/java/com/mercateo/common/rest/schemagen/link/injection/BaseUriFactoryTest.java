package com.mercateo.common.rest.schemagen.link.injection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import com.mercateo.common.rest.schemagen.link.helper.BaseUriCreator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BaseUriFactoryTest {

    @Mock
    private UriInfo uriInfo;

    @Mock
    private HttpHeaders httpHeaders;

    @Mock
    private BaseUriCreator baseUriCreator;

    @InjectMocks
    private BaseUriFactory baseUriFactory;

    private URI defaultBaseUri;

    private URI baseUri;

    @Mock
    private MultivaluedMap<String, String> requestHeaders;

    @Before
    public void setUp() throws Exception {
        defaultBaseUri = new URI("http://host/default");
        baseUri = new URI("http://host/path");
    }

    @Test
    public void shouldProvideBaseUri() throws Exception {
        when(uriInfo.getBaseUri()).thenReturn(defaultBaseUri);
        when(httpHeaders.getRequestHeaders()).thenReturn(requestHeaders);
        when(baseUriCreator.createBaseUri(defaultBaseUri, requestHeaders)).thenReturn(baseUri);

        final BaseUri providedBaseUri = baseUriFactory.provide();

        assertThat(providedBaseUri.get()).isEqualTo(baseUri);
    }

    @Test
    public void disposeShouldDoNothing() throws Exception {
        baseUriFactory.dispose(new BaseUri(baseUri));

        verifyNoMoreInteractions(uriInfo, httpHeaders, baseUriCreator);
    }
}