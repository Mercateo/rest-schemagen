package com.mercateo.common.rest.schemagen.link.relation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class RelationEnumTest{
    enum TestRelation implements RelationEnum<TestRelation>{
        TEST_RELATION
    }
    
    @Test
    public void createsDefaultRelationContainer() throws Exception {
        assertThat(TestRelation.TEST_RELATION.getRelation().getName()).isEqualTo("test-relation");
    }
}
