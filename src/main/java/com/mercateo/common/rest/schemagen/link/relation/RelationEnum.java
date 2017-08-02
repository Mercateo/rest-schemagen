package com.mercateo.common.rest.schemagen.link.relation;

public interface RelationEnum<E extends Enum<E> & RelationEnum<E>> extends RelationContainer {
    @Override
    default public Relation getRelation() {
        return Relation.of(((Enum<?>)this).name().toLowerCase().replace('_', '-'));
    }
}
