package com.mercateo.common.rest.schemagen.link.relation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class RelationTypeDefault0Test {

    public static final String NAME = "<name>";
    public static final String SERIALIZED_NAME = "<NAME>";
    public static final boolean SHOULD_BE_SERIALIZED = true;
    private RelationTypeDefault relationType;

    @Before
    public void setUp() {
        relationType = new RelationTypeDefault(NAME, SHOULD_BE_SERIALIZED, SERIALIZED_NAME);
    }

    @Test
    public void testGetName() {
        assertThat(relationType.getName()).isEqualTo(NAME);
    }

    @Test
    public void testIsShouldBeSerialized() {
        assertThat(relationType.isShouldBeSerialized()).isTrue();
    }

    @Test
    public void testGetSerializedName() {
        assertThat(relationType.getSerializedName()).isEqualTo(SERIALIZED_NAME);
    }

}