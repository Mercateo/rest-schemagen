/*
 * Copyright © 2015 Mercateo AG (http://www.mercateo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mercateo.common.rest.schemagen.link.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

@ExtendWith(MockitoExtension.class)
public class BaseUriCreatorDefaultTest {

    private MultivaluedMap<String, String> requestHeaders;

    private URI defaultBaseUri;

    private BaseUriCreator baseUriFactory;

    @BeforeEach
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
