package com.mercateo.common.rest.schemagen.link.injection;

import static java.util.Objects.requireNonNull;

import java.net.URI;

public class BaseUri {

    private final URI baseUri;

    public BaseUri(URI baseUri) {
        this.baseUri = requireNonNull(baseUri);
    }

    protected BaseUri() {
        this.baseUri = null;
    }

    public URI get() {
        return baseUri;
    }

    @Override
    public String toString() {
        return baseUri.toString();
    }
}
