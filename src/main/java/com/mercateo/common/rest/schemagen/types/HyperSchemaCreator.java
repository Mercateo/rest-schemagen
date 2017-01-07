package com.mercateo.common.rest.schemagen.types;

import com.mercateo.common.rest.schemagen.JsonHyperSchema;
import com.mercateo.common.rest.schemagen.JsonHyperSchemaCreator;

import javax.inject.Inject;
import javax.ws.rs.core.Link;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.mercateo.common.rest.schemagen.util.OptionalUtil.collect;

public class HyperSchemaCreator {

    private final ObjectWithSchemaCreator objectWithSchemaCreator;

    private final JsonHyperSchemaCreator jsonHyperSchemaCreator;

    @Inject
    public HyperSchemaCreator(ObjectWithSchemaCreator objectWithSchemaCreator, JsonHyperSchemaCreator jsonHyperSchemaCreator) {
        this.objectWithSchemaCreator = objectWithSchemaCreator;
        this.jsonHyperSchemaCreator = jsonHyperSchemaCreator;
    }

    @SafeVarargs
    public final <T> ObjectWithSchema<T> create(T object, Optional<Link>... links) {
        JsonHyperSchema hyperSchema = jsonHyperSchemaCreator.from(collect(links));
        return objectWithSchemaCreator.create(object, hyperSchema);
    }

    @SafeVarargs
    public final <T> ObjectWithSchema<T> create(T object, List<Link>... linkArray) {
        ArrayList<Link> links = new ArrayList<>();
        Arrays.stream(linkArray).forEach(links::addAll);

        JsonHyperSchema hyperSchema = jsonHyperSchemaCreator.from(links);
        return objectWithSchemaCreator.create(object, hyperSchema);
    }
}
