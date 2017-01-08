package com.mercateo.common.rest.schemagen.link.injection;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import org.glassfish.hk2.api.Factory;

import com.mercateo.common.rest.schemagen.link.helper.BaseUriCreator;

public class BaseUriFactory implements Factory<BaseUri> {

    private final UriInfo uriInfo;

    private final HttpHeaders httpHeaders;

    private final BaseUriCreator baseUriCreator;

    @Inject
    public BaseUriFactory(UriInfo uriInfo, HttpHeaders httpHeaders, BaseUriCreator baseUriCreator) {
        this.uriInfo = uriInfo;
        this.httpHeaders = httpHeaders;
        this.baseUriCreator = baseUriCreator;
    }

    @Override
    public BaseUri provide() {
        return new BaseUri(baseUriCreator.createBaseUri(uriInfo.getBaseUri(), httpHeaders.getRequestHeaders()));
    }

    @Override
    public void dispose(BaseUri uri) {
        // no body
    }
}
