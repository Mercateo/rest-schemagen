package com.mercateo.common.rest.schemagen.link.relation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class RelationDefault0Test {

    private RelationTypeDefault type;

    private RelationDefault relation;

    private String relationName;

    @Before
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