package com.mercateo.common.rest.schemagen.link.relation;

import com.google.common.base.Objects;

public class RelationTypeDefault implements RelationType {
    private final String name;

    private final boolean shouldBeSerialized;

    private final String serializedName;

    public RelationTypeDefault(String name, boolean shouldBeSerialized, String serializedName) {
        this.name = name;
        this.shouldBeSerialized = shouldBeSerialized;
        this.serializedName = serializedName;
    }

    @Override
    public boolean isShouldBeSerialized() {
        return shouldBeSerialized;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }

    @SuppressWarnings("boxing")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RelationTypeDefault)) return false;
        RelationTypeDefault that = (RelationTypeDefault) o;
        return Objects.equal(isShouldBeSerialized(), that.isShouldBeSerialized()) &&
                Objects.equal(getName(), that.getName()) &&
                Objects.equal(getSerializedName(), that.getSerializedName());
    }

    @SuppressWarnings("boxing")
    @Override
    public int hashCode() {
        return Objects.hashCode(getName(), isShouldBeSerialized(), getSerializedName());
    }
}
