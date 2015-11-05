package com.mercateo.common.rest.schemagen.link;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.ArrayList;

import com.google.common.annotations.VisibleForTesting;
import com.mercateo.common.rest.schemagen.JerseyResource;
import com.mercateo.common.rest.schemagen.JsonSchemaGenerator;
import com.mercateo.common.rest.schemagen.RestJsonSchemaGenerator;
import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;
import com.mercateo.common.rest.schemagen.plugin.MethodCheckerForLink;

public class LinkMetaFactory {

    private final LinkFactoryContext linkFactoryContext;

    LinkMetaFactory(JsonSchemaGenerator schemaGenerator, URI baseUri,
            MethodCheckerForLink methodCheckerForLink,
            FieldCheckerForSchema fieldCheckerForSchema) {
        this(new LinkFactoryContext(schemaGenerator, baseUri, methodCheckerForLink,
                fieldCheckerForSchema));
    }

    protected LinkMetaFactory() {
        this(null);
    }

    LinkMetaFactory(LinkFactoryContext linkFactoryContext) {
        this.linkFactoryContext = linkFactoryContext;
    }

    public static LinkMetaFactory create(JsonSchemaGenerator jsonSchemaGenerator, URI baseUri,
            MethodCheckerForLink methodCheckerForLink,
            FieldCheckerForSchema fieldCheckerForSchema) {

        checkNotNull(baseUri);
        checkNotNull(methodCheckerForLink);
        checkNotNull(fieldCheckerForSchema);
        return new LinkMetaFactory(jsonSchemaGenerator, baseUri, methodCheckerForLink,
                fieldCheckerForSchema);
    }

    @VisibleForTesting
    public static LinkMetaFactory createInsecureFactoryForTest() {
        return new LinkMetaFactory(new RestJsonSchemaGenerator(), URI.create(""), r -> true, (r,
                c) -> true);
    }

    public <T extends JerseyResource> LinkFactory<T> createFactoryFor(Class<T> resourceClass) {
        return new LinkFactory<>(resourceClass, linkFactoryContext, new ArrayList<>());
    }

}
