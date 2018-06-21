/**
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

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import com.mercateo.common.rest.schemagen.link.LinkFactoryContextDefault;
import org.glassfish.hk2.api.Factory;

import com.mercateo.common.rest.schemagen.link.LinkFactoryContext;
import com.mercateo.common.rest.schemagen.link.helper.BaseUriCreator;
import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;
import com.mercateo.common.rest.schemagen.plugin.MethodCheckerForLink;

import java.net.URI;

public class LinkFactoryContextFactory implements Factory<LinkFactoryContext> {

    private final UriInfo uriInfo;

    private final HttpHeaders httpHeaders;

    private final BaseUriCreator baseUriCreator;

    private final MethodCheckerForLink methodCheckerForLink;

    private final FieldCheckerForSchema fieldCheckerForSchema;

    @Inject
    public LinkFactoryContextFactory(UriInfo uriInfo, HttpHeaders httpHeaders, BaseUriCreator baseUriCreator,
            MethodCheckerForLink methodCheckerForLink, FieldCheckerForSchema fieldCheckerForSchema) {
        this.uriInfo = uriInfo;
        this.httpHeaders = httpHeaders;
        this.baseUriCreator = baseUriCreator;
        this.methodCheckerForLink = methodCheckerForLink;
        this.fieldCheckerForSchema = fieldCheckerForSchema;
    }

    @Override
    public LinkFactoryContext provide() {
        final URI baseUri = baseUriCreator.createBaseUri(uriInfo.getBaseUri(), httpHeaders.getRequestHeaders());
        return new LinkFactoryContextDefault(baseUri, methodCheckerForLink, fieldCheckerForSchema);
    }

    @Override
    public void dispose(LinkFactoryContext arg0) {
        // nothing
    }
}
