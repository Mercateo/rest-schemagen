package com.mercateo.common.rest.schemagen.link.relation;

public interface RelationContainer {

    static RelationContainer of(String name) {
        return () -> Relation.of(name);
    }

    Relation getRelation();
}
