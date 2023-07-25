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

import org.junit.jupiter.api.Test;

import com.mercateo.common.rest.schemagen.JsonHyperSchema;

public class ObjectWithSchemaCreatorTest {
    @Test
    public void shouldWrapObjectAndSchema() throws Exception {
        Object object = new Object();
        JsonHyperSchema schema = mock(JsonHyperSchema.class);

        ObjectWithSchema<Object> objectWithSchema = new ObjectWithSchemaCreator().create(object, schema);

        assertThat(objectWithSchema.getObject()).isEqualTo(object);
        assertThat(objectWithSchema.getSchema()).isEqualTo(schema);
    }
}
