package com.mercateo.common.rest.schemagen.link.relation;

public interface RelationContainer {
    
    static public RelationContainer of(String name) {
        return new RelationContainer() {
            @Override
            public Relation getRelation() {
                return Relation.of(name);
            }
        };
    }
    
    Relation getRelation();
}
