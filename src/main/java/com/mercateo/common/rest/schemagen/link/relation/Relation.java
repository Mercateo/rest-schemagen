package com.mercateo.common.rest.schemagen.link.relation;

public interface Relation {

    RelationType DEFAULT_TYPE = RelType.SELF.getRelationType();

    String getName();

    RelationType getType();

    static <E extends Enum<E> & RelationContainer> Relation of(E relation) {
        return of(relation, DEFAULT_TYPE);
    }

    static <E extends Enum<E> & RelationContainer> Relation of(E relation, RelationTypeContainer typeContainer) {
        return of(relation, typeContainer.getRelationType());
    }

    static <E extends Enum<E> & RelationContainer> Relation of(E relation, RelationType type) {
        return of(relation.name().toLowerCase().replace('_', '-'), type);
    }

    static Relation of(String name, RelationTypeContainer typeContainer) {
        return of(name, typeContainer.getRelationType());
    }

    static Relation of(String name) {
        return new RelationDefault(name, DEFAULT_TYPE);
    }

    static Relation of(String name, RelationType type) {
        return new RelationDefault(name, type);
    }
}
