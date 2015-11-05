package com.mercateo.common.rest.schemagen.link.relation;


public enum Rel implements RelationContainer {

    SELF, CANONICAL, CREATE, UPDATE, DELETE, PREV, NEXT, FIRST, LAST, MOVE;

    private final Relation relation;

    Rel() {
        relation = Relation.of(this, RelType.SELF);
    }

    @Override
    public Relation getRelation() {
        return relation;
    }
}
