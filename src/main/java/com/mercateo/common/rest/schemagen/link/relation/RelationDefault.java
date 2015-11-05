package com.mercateo.common.rest.schemagen.link.relation;

import com.google.common.base.Objects;

public class RelationDefault implements Relation {

    private final String name;
    private final RelationType type;

    RelationDefault(String name, RelationType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public RelationType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RelationDefault)) return false;
        RelationDefault that = (RelationDefault) o;
        return Objects.equal(getName(), that.getName()) &&
                Objects.equal(getType(), that.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName(), getType());
    }

    @Override
    public String toString() {
        return "RelationDefault [name=" + name + ", type=" + type + "]";
    }
}
