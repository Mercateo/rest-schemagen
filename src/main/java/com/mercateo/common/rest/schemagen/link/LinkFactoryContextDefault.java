package com.mercateo.common.rest.schemagen.link;

import static java.util.Objects.requireNonNull;

import java.net.URI;

import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;
import com.mercateo.common.rest.schemagen.plugin.MethodCheckerForLink;

public class LinkFactoryContextDefault implements LinkFactoryContext {

    private final URI baseUri;

    private final MethodCheckerForLink methodCheckerForLink;

    private final FieldCheckerForSchema fieldCheckerForSchema;

    public LinkFactoryContextDefault() {
        baseUri = null;
        methodCheckerForLink = null;
        fieldCheckerForSchema = null;
    }

    public LinkFactoryContextDefault(URI baseUri, MethodCheckerForLink methodCheckerForLink,
                                     FieldCheckerForSchema fieldCheckerForSchema) {
        this.baseUri = baseUri;
        this.methodCheckerForLink = requireNonNull(methodCheckerForLink);
        this.fieldCheckerForSchema = requireNonNull(fieldCheckerForSchema);
    }

    @Override
    public URI getBaseUri() {
        return baseUri;
    }

    @Override
    public FieldCheckerForSchema getFieldCheckerForSchema() {
        return fieldCheckerForSchema;
    }

    @Override
    public MethodCheckerForLink getMethodCheckerForLink() {
        return methodCheckerForLink;
    }
}
