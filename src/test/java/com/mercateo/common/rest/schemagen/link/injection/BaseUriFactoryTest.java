package com.mercateo.common.rest.schemagen.link.injection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BaseUriFactoryTest {

    @Mock
    private HttpHeaders httpHeaders;

    @Mock
    private UriInfo uriInfo;

    private BaseUriFactory baseUriFactory;

    @Before
    public void setUp() throws URISyntaxException {
        baseUriFactory = new BaseUriFactory(uriInfo, httpHeaders);

        when(uriInfo.getBaseUri()).thenReturn(new URI("http://host:8090/base/"));
    }

    @Test
    public void testWithoutAnyHeaders() {
        BaseUri baseUri = baseUriFactory.provide();

        assertThat(baseUri.toString()).isEqualTo("http://host:8090/base/");
    }

    @Test
    public void testWithProtocolHeader() {
        when(httpHeaders.getRequestHeader(BaseUriFactory.TLS_STATUS_HEADER)).thenReturn(Collections
                .singletonList("Off"));
        BaseUri baseUri = baseUriFactory.provide();

        assertThat(baseUri.toString()).isEqualTo("http://host/base/");
    }

    @Test
    public void testWithHostHeader() {
        when(httpHeaders.getRequestHeader(BaseUriFactory.HOST_HEADER)).thenReturn(Collections
                .singletonList("server"));
        BaseUri baseUri = baseUriFactory.provide();

        assertThat(baseUri.toString()).isEqualTo("http://server/base/");
    }

    @Test
    public void testWithMultipleHostHeaders() {
        when(httpHeaders.getRequestHeader(BaseUriFactory.HOST_HEADER)).thenReturn(Collections
                .singletonList("firstServer, secondServer"));
        BaseUri baseUri = baseUriFactory.provide();

        assertThat(baseUri.toString()).isEqualTo("http://firstServer/base/");
    }

    @Test
    public void testWithBasePathHeader() {
        when(httpHeaders.getRequestHeader(BaseUriFactory.SERVICE_BASE_HEADER)).thenReturn(
                Collections.singletonList("/service-api/"));
        BaseUri baseUri = baseUriFactory.provide();

        assertThat(baseUri.toString()).isEqualTo("http://host:8090/service-api/");
    }

    @Test
    public void testWithAllHeaders() {
        when(httpHeaders.getRequestHeader(BaseUriFactory.TLS_STATUS_HEADER)).thenReturn(Collections
                .singletonList("On"));
        when(httpHeaders.getRequestHeader(BaseUriFactory.HOST_HEADER)).thenReturn(Collections
                .singletonList("server"));
        when(httpHeaders.getRequestHeader(BaseUriFactory.SERVICE_BASE_HEADER)).thenReturn(
                Collections.singletonList("/service-api/"));

        BaseUri baseUri = baseUriFactory.provide();

        assertThat(baseUri.toString()).isEqualTo("https://server/service-api/");
    }

    @Test
    public void testHttpsWithNoHeaders() throws URISyntaxException {
        when(uriInfo.getBaseUri()).thenReturn(new URI("https://www.mercateo.com/"));
        BaseUri baseUri = baseUriFactory.provide();
        assertEquals("https", baseUri.get().getScheme());
    }
}