package com.mercateo.common.rest.schemagen.link;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import com.mercateo.common.rest.schemagen.JsonSchemaGenerator;
import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;
import com.mercateo.common.rest.schemagen.plugin.MethodCheckerForLink;

public class LinkFactoryContext {
    private final JsonSchemaGenerator schemaGenerator;

    private final URI baseUri;

    private final MethodCheckerForLink methodCheckerForLink;

    private final FieldCheckerForSchema fieldCheckerForSchema;

    LinkFactoryContext(JsonSchemaGenerator schemaGenerator, URI baseUri,
            MethodCheckerForLink methodCheckerForLink,
            FieldCheckerForSchema fieldCheckerForSchema) {
        this.schemaGenerator = checkNotNull(schemaGenerator);
        this.baseUri = checkNotNull(baseUri);
        this.methodCheckerForLink = checkNotNull(methodCheckerForLink);
        this.fieldCheckerForSchema = checkNotNull(fieldCheckerForSchema);

    }

    public JsonSchemaGenerator getSchemaGenerator() {
        return schemaGenerator;
    }

    public URI getBaseUri() {
        return baseUri;
    }

    public FieldCheckerForSchema getFieldCheckerForSchema() {
        return fieldCheckerForSchema;
    }

    public MethodCheckerForLink getMethodCheckerForLink() {
        return methodCheckerForLink;
    }

}
