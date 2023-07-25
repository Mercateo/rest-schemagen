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
package com.mercateo.common.rest.schemagen.link.injection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mercateo.common.rest.schemagen.JsonSchemaGenerator;
import com.mercateo.common.rest.schemagen.link.LinkFactoryContext;
import com.mercateo.common.rest.schemagen.link.LinkMetaFactory;

@ExtendWith(MockitoExtension.class)
public class LinkMetaFactoryFactoryTest {

    @Mock
    private JsonSchemaGenerator schemaGenerator;

    @Mock
    private LinkFactoryContext linkFactoryContext;

    @InjectMocks
    private LinkMetaFactoryFactory linkMetaFactoryFactory;

    @Test
    public void testProvideLinkMetaFactory() {
        final LinkMetaFactory linkMetaFactory = linkMetaFactoryFactory.provide();

        assertThat(linkMetaFactory.getSchemaGenerator()).isEqualTo(schemaGenerator);
        assertThat(linkMetaFactory.getFactoryContext()).isEqualTo(linkFactoryContext);
    }

    @Test
    public void disposeShouldDoNothing() throws Exception {
        final LinkMetaFactory linkMetaFactory = mock(LinkMetaFactory.class);
        linkMetaFactoryFactory.dispose(linkMetaFactory);

        verifyNoInteractions(schemaGenerator, linkFactoryContext, linkMetaFactory);
    }
}
