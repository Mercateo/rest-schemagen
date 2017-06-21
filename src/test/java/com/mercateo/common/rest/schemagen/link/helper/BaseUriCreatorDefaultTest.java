package com.mercateo.common.rest.schemagen.link.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BaseUriCreatorDefaultTest {

    private MultivaluedMap<String, String> requestHeaders;

    private URI defaultBaseUri;

    private BaseUriCreator baseUriFactory;

    @Before
    public void setUp() throws URISyntaxException {
        defaultBaseUri = new URI("http://host:8090/base/");
        requestHeaders = new MultivaluedHashMap<>();
        baseUriFactory = new BaseUriCreatorDefault();
    }

    @Test
    public void testWithoutAnyHeaders() {
        URI baseUri = baseUriFactory.createBaseUri(defaultBaseUri, requestHeaders);

        assertThat(baseUri.toString()).isEqualTo("http://host:8090/base/");
    }

    @Test
    public void testWithCustomProtocolHeader() {
        requestHeaders.putSingle(BaseUriCreatorDefault.TLS_STATUS_HEADER, "Off");

        URI baseUri = baseUriFactory.createBaseUri(defaultBaseUri, requestHeaders);

        assertThat(baseUri.toString()).isEqualTo("http://host/base/");
    }

    @Test
    public void testWithProtocolHeader() {
        requestHeaders.putSingle(BaseUriCreatorDefault.FORWARDED_PROTO_HEADER, "Https, http");

        URI baseUri = baseUriFactory.createBaseUri(defaultBaseUri, requestHeaders);

        assertThat(baseUri.toString()).isEqualTo("https://host/base/");
    }

    @Test
    public void testWithHostHeader() {
        requestHeaders.putSingle(BaseUriCreatorDefault.FORWARDED_HOST_HEADER, "server");

        URI baseUri = baseUriFactory.createBaseUri(defaultBaseUri, requestHeaders);

        assertThat(baseUri.toString()).isEqualTo("http://server/base/");
    }

    @Test
    public void testWithMultipleHostHeaders() {
        requestHeaders.putSingle(BaseUriCreatorDefault.FORWARDED_HOST_HEADER, "firstServer, secondServer");

        URI baseUri = baseUriFactory.createBaseUri(defaultBaseUri, requestHeaders);

        assertThat(baseUri.toString()).isEqualTo("http://firstServer/base/");
    }

    @Test
    public void testWithBasePathHeader() {
        requestHeaders.putSingle(BaseUriCreatorDefault.SERVICE_BASE_HEADER, "/service-api/");

        URI baseUri = baseUriFactory.createBaseUri(defaultBaseUri, requestHeaders);

        assertThat(baseUri.toString()).isEqualTo("http://host:8090/service-api/");
    }

    @Test
    public void testWithAllHeaders() {
        requestHeaders.putSingle(BaseUriCreatorDefault.TLS_STATUS_HEADER, "On");
        requestHeaders.putSingle(BaseUriCreatorDefault.FORWARDED_HOST_HEADER, "server");
        requestHeaders.putSingle(BaseUriCreatorDefault.SERVICE_BASE_HEADER, "/service-api/");

        URI baseUri = baseUriFactory.createBaseUri(defaultBaseUri, requestHeaders);

        assertThat(baseUri.toString()).isEqualTo("https://server/service-api/");
    }

    @Test
    public void testHttpsWithNoHeaders() throws URISyntaxException {
        defaultBaseUri = new URI("https://www.mercateo.com/");

        URI baseUri = baseUriFactory.createBaseUri(defaultBaseUri, requestHeaders);
        assertThat(baseUri.getScheme()).isEqualTo("https");
    }

    @Test
    public void shouldRethrowUriSyntaxException() {
        requestHeaders.putSingle(BaseUriCreatorDefault.FORWARDED_HOST_HEADER, ".");

        assertThatThrownBy(() -> baseUriFactory.createBaseUri(defaultBaseUri, new HttpRequestHeaders(requestHeaders)))
                .isInstanceOf(IllegalStateException.class)
                .hasCauseExactlyInstanceOf(URISyntaxException.class);
    }
}