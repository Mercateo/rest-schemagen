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
package com.mercateo.common.rest.schemagen.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mercateo.common.rest.schemagen.JsonHyperSchemaCreator;

import jakarta.ws.rs.core.Link;

@ExtendWith(MockitoExtension.class)
public class HyperSchemaCreatorTest {

    @Mock
    private Object object;

    @Spy
    private ObjectWithSchemaCreator objectWithSchemaCreator;

    @Spy
    private JsonHyperSchemaCreator jsonHyperSchemaCreator;

    @InjectMocks
    private HyperSchemaCreator hyperSchemaCreator;

    @Test
    public void shouldWrapPayload() {
        ObjectWithSchema<Object> objectWithSchema = hyperSchemaCreator.create(object, Optional.of(mock(Link.class)));

        assertThat(objectWithSchema.getObject()).isEqualTo(object);
    }

    @Test
    public void shouldIgnoreNotExistentLinks() throws Exception {
        ObjectWithSchema<Object> objectWithSchema = hyperSchemaCreator.create(object, Optional.empty());

        assertThat(objectWithSchema.getSchema().getLinks()).isEmpty();
    }

    @Test
    public void shouldAccumulateOptionalLinks() throws Exception {
        Link link1 = mock(Link.class);
        Link link2 = mock(Link.class);

        ObjectWithSchema<Object> objectWithSchema = hyperSchemaCreator
                .create(object, Optional.of(link1), Optional.of(link2));

        assertThat(objectWithSchema.getSchema().getLinks()).containsExactly(link1, link2);
    }

    @Test
    public void shouldAccumulateLinkCollections() throws Exception {
        Link link1 = mock(Link.class);
        Link link2 = mock(Link.class);

        ObjectWithSchema<Object> objectWithSchema = hyperSchemaCreator
                .create(object, Collections.singletonList(link1), Collections.singletonList(link2));

        assertThat(objectWithSchema.getSchema().getLinks()).containsExactly(link1, link2);
    }
}
