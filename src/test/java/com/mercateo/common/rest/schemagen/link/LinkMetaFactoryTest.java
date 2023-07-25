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
package com.mercateo.common.rest.schemagen.link;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mercateo.common.rest.schemagen.JsonSchemaGenerator;
import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;
import com.mercateo.common.rest.schemagen.plugin.MethodCheckerForLink;
import com.mercateo.common.rest.schemagen.plugin.TargetSchemaEnablerForLink;

@ExtendWith(MockitoExtension.class)
public class LinkMetaFactoryTest {

    @Mock
    private JsonSchemaGenerator schemaGenerator;

    @Mock
    private LinkFactoryContext linkFactoryContext;

    @Mock
    private TargetSchemaEnablerForLink targetSchemaEnablerForLink;

    @Test
    public void shouldCreateDeprecatedFactory() {
        final LinkMetaFactory linkMetaFactory = LinkMetaFactory.create(schemaGenerator, linkFactoryContext);

        assertThat(linkMetaFactory.getSchemaGenerator()).isEqualTo(schemaGenerator);
        assertThat(linkMetaFactory.getFactoryContext()).isEqualTo(linkFactoryContext);
    }

    @Test
    public void shouldCreateFactory() {
        final LinkMetaFactory linkMetaFactory = LinkMetaFactory.create(schemaGenerator);

        assertThat(linkMetaFactory.getSchemaGenerator()).isEqualTo(schemaGenerator);
        assertThat(linkMetaFactory.getFactoryContext()).isNull();
        assertThat(linkMetaFactory.externalLinkFactory()).isNotNull();
    }

    @Test
    public void shouldCreateFactoryFromRequestScopedParts() throws Exception {
        final URI baseUri = new URI("http://host/path");
        final FieldCheckerForSchema fieldCheckerForSchema = mock(FieldCheckerForSchema.class);
        final MethodCheckerForLink methodCheckerForLink = mock(MethodCheckerForLink.class);
        final LinkMetaFactory linkMetaFactory = LinkMetaFactory
                .create(schemaGenerator, baseUri, methodCheckerForLink, fieldCheckerForSchema,
                        targetSchemaEnablerForLink);

        assertThat(linkMetaFactory.getSchemaGenerator()).isEqualTo(schemaGenerator);
        final LinkFactoryContext factoryContext = linkMetaFactory.getFactoryContext();
        assertThat(factoryContext).isNotNull();
        assertThat(factoryContext.getBaseUri()).isEqualTo(baseUri);
        assertThat(factoryContext.getMethodCheckerForLink()).isEqualTo(methodCheckerForLink);
        assertThat(factoryContext.getFieldCheckerForSchema()).isEqualTo(fieldCheckerForSchema);
        assertThat(factoryContext.getTargetSchemaEnablerForLink()).isEqualTo(targetSchemaEnablerForLink);
        assertThat(linkMetaFactory.externalLinkFactory()).isNotNull();
    }

    @Test
    public void insecureFactoryForTest() {
        final LinkMetaFactory linkMetaFactory = LinkMetaFactory.createInsecureFactoryForTest();

        assertThat(linkMetaFactory.getSchemaGenerator()).isNotNull();
        assertThat(linkMetaFactory.getFactoryContext()).isNotNull();
        assertThat(linkMetaFactory.externalLinkFactory()).isNotNull();
    }
}
