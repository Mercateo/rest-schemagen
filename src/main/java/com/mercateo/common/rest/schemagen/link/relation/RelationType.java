package com.mercateo.common.rest.schemagen.link.relation;

public interface RelationType {
    boolean isShouldBeSerialized();

    String getName();

    String getSerializedName();

    static <E extends Enum<E> & RelationTypeContainer> RelationType of(E typeContainer, boolean shouldBeSerialized, String serializedName) {
        return new RelationTypeDefault(typeContainer.name().toLowerCase(), shouldBeSerialized, serializedName);
    }
}
