package com.mercateo.common.rest.schemagen.link.helper;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BaseUriCreatorTest {

    private MultivaluedMap<String, String> requestHeaders;

    private URI defaultBaseUri;

    private BaseUriCreator baseUriFactory;

    @Before
    public void setUp() throws URISyntaxException {
        defaultBaseUri = new URI("http://host:8090/base/");
        requestHeaders = new MultivaluedHashMap<>();
        baseUriFactory = new BaseUriCreator();
    }

    @Test
    public void testWithoutAnyHeaders() {
        URI baseUri = baseUriFactory.createBaseUri(defaultBaseUri, requestHeaders);

        assertThat(baseUri.toString()).isEqualTo("http://host:8090/base/");
    }

    @Test
    public void testWithProtocolHeader() {
        requestHeaders.putSingle(BaseUriCreator.TLS_STATUS_HEADER, "Off");

        URI baseUri = baseUriFactory.createBaseUri(defaultBaseUri, requestHeaders);

        assertThat(baseUri.toString()).isEqualTo("http://host/base/");
    }

    @Test
    public void testWithHostHeader() {
        requestHeaders.putSingle(BaseUriCreator.HOST_HEADER, "server");

        URI baseUri = baseUriFactory.createBaseUri(defaultBaseUri, requestHeaders);

        assertThat(baseUri.toString()).isEqualTo("http://server/base/");
    }

    @Test
    public void testWithMultipleHostHeaders() {
        requestHeaders.putSingle(BaseUriCreator.HOST_HEADER, "firstServer, secondServer");

        URI baseUri = baseUriFactory.createBaseUri(defaultBaseUri, requestHeaders);

        assertThat(baseUri.toString()).isEqualTo("http://firstServer/base/");
    }

    @Test
    public void testWithBasePathHeader() {
        requestHeaders.putSingle(BaseUriCreator.SERVICE_BASE_HEADER, "/service-api/");

        URI baseUri = baseUriFactory.createBaseUri(defaultBaseUri, requestHeaders);

        assertThat(baseUri.toString()).isEqualTo("http://host:8090/service-api/");
    }

    @Test
    public void testWithAllHeaders() {
        requestHeaders.putSingle(BaseUriCreator.TLS_STATUS_HEADER, "On");
        requestHeaders.putSingle(BaseUriCreator.HOST_HEADER, "server");
        requestHeaders.putSingle(BaseUriCreator.SERVICE_BASE_HEADER, "/service-api/");

        URI baseUri = baseUriFactory.createBaseUri(defaultBaseUri, requestHeaders);

        assertThat(baseUri.toString()).isEqualTo("https://server/service-api/");
    }

    @Test
    public void testHttpsWithNoHeaders() throws URISyntaxException {
        defaultBaseUri = new URI("https://www.mercateo.com/");

        URI baseUri = baseUriFactory.createBaseUri(defaultBaseUri, requestHeaders);
        assertThat(baseUri.getScheme()).isEqualTo("https");
    }
}