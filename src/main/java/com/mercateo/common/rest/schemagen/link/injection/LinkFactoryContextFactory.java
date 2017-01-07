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
