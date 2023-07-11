package com.mercateo.common.rest.schemagen.link.relation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RelationDefault0Test {

    private RelationTypeDefault type;

    private RelationDefault relation;

    private String relationName;

    @BeforeEach
    public void setUp() {
        type = new RelationTypeDefault("<typeName>", false, "<type>");
        relationName = "<name>";
        relation = new RelationDefault(relationName, type);
    }

    @Test
    public void testGetName() {
        assertThat(relation.getName()).isEqualTo(relationName);
    }

    @Test
    public void testGetType() {
        assertThat(relation.getType()).isEqualTo(type);
    }
}