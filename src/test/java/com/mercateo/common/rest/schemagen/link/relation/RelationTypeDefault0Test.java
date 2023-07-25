/*
 * Copyright © 2015 Mercateo AG (http://www.mercateo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mercateo.common.rest.schemagen.link.relation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RelationTypeDefault0Test {

    public static final String NAME = "<name>";
    public static final String SERIALIZED_NAME = "<NAME>";
    public static final boolean SHOULD_BE_SERIALIZED = true;
    private RelationTypeDefault relationType;

    @BeforeEach
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
