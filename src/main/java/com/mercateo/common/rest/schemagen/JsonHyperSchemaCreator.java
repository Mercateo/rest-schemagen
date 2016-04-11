package com.mercateo.common.rest.schemagen;

import java.util.Collection;

import javax.ws.rs.core.Link;

import com.mercateo.common.rest.schemagen.JsonHyperSchema;

public class JsonHyperSchemaCreator {

    public JsonHyperSchema from(Collection<Link> links) {
        return JsonHyperSchema.from(links);
    }

}
