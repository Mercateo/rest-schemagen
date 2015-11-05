package com.mercateo.common.rest.schemagen.link.relation;

public enum RelType implements RelationTypeContainer {

    SELF(false, "_self"), OTHER(true, "_blank"), INHERITED(true, "_parent");

    private final RelationType type;

    RelType(boolean shouldBeSerialized, String serializedName) {
        type = RelationType.of(this, shouldBeSerialized, serializedName);
    }

    @Override
    public RelationType getRelationType() {
        return type;
    }
}
