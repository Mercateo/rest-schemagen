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
package com.mercateo.common.rest.schemagen.link.injection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mercateo.common.rest.schemagen.link.LinkFactoryContext;
import com.mercateo.common.rest.schemagen.link.helper.BaseUriCreator;
import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;
import com.mercateo.common.rest.schemagen.plugin.MethodCheckerForLink;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;

@ExtendWith(MockitoExtension.class)
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
