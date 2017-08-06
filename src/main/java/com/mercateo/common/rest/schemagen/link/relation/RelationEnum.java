package com.mercateo.common.rest.schemagen.link.relation;

public interface RelationEnum extends RelationContainer {
    
    public String name();

    @Override
    default public Relation getRelation() {
        return Relation.of(name().toLowerCase().replace('_', '-'));
    }

}
