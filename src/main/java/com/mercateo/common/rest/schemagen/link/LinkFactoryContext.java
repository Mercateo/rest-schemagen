package com.mercateo.common.rest.schemagen.link;

import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;
import com.mercateo.common.rest.schemagen.plugin.MethodCheckerForLink;

import java.net.URI;

public interface LinkFactoryContext {
    URI getBaseUri();

    FieldCheckerForSchema getFieldCheckerForSchema();

    MethodCheckerForLink getMethodCheckerForLink();
}
