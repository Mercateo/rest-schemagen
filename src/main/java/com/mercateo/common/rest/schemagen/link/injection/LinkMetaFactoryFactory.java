package com.mercateo.common.rest.schemagen.link.injection;

import javax.inject.Inject;

import org.glassfish.hk2.api.Factory;

import com.mercateo.common.rest.schemagen.JsonSchemaGenerator;
import com.mercateo.common.rest.schemagen.link.LinkFactoryContext;
import com.mercateo.common.rest.schemagen.link.LinkMetaFactory;

public class LinkMetaFactoryFactory implements Factory<LinkMetaFactory> {

    private final JsonSchemaGenerator jsonSchemaGenerator;

    private final LinkFactoryContext linkFactoryContext;

    @Inject
    public LinkMetaFactoryFactory(JsonSchemaGenerator jsonSchemaGenerator, LinkFactoryContext linkFactoryContext) {
        this.jsonSchemaGenerator = jsonSchemaGenerator;
        this.linkFactoryContext = linkFactoryContext;
    }

    @Override
    public void dispose(LinkMetaFactory arg0) {
        // nothing
    }

    @Override
    public LinkMetaFactory provide() {
        return LinkMetaFactory.create(jsonSchemaGenerator, linkFactoryContext);
    }
}
