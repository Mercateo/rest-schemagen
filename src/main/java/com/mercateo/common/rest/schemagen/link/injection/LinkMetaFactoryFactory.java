package com.mercateo.common.rest.schemagen.link.injection;

import javax.inject.Inject;

import org.glassfish.hk2.api.Factory;

import com.mercateo.common.rest.schemagen.JsonSchemaGenerator;
import com.mercateo.common.rest.schemagen.link.LinkMetaFactory;
import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;
import com.mercateo.common.rest.schemagen.plugin.MethodCheckerForLink;

public class LinkMetaFactoryFactory implements Factory<LinkMetaFactory> {

    private final JsonSchemaGenerator jsonSchemaGenerator;

    private BaseUri baseUri;

    private MethodCheckerForLink methodCheckerForLink;

    private FieldCheckerForSchema fieldCheckerForSchema;

    @Inject
    public LinkMetaFactoryFactory(JsonSchemaGenerator jsonSchemaGenerator, BaseUri baseUri,
            MethodCheckerForLink methodCheckerForLink,
            FieldCheckerForSchema fieldCheckerForSchema) {
        this.jsonSchemaGenerator = jsonSchemaGenerator;
        this.baseUri = baseUri;
        this.methodCheckerForLink = methodCheckerForLink;
        this.fieldCheckerForSchema = fieldCheckerForSchema;
    }

    @Override
    public void dispose(LinkMetaFactory arg0) {
        // nothing
    }

    @Override
    public LinkMetaFactory provide() {
        return LinkMetaFactory.create(jsonSchemaGenerator, baseUri.get(), methodCheckerForLink,
                fieldCheckerForSchema);
    }
}
