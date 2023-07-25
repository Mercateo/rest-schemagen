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
package com.mercateo.common.rest.schemagen.plugin.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mercateo.common.rest.schemagen.link.Scope;
import com.mercateo.common.rest.schemagen.plugin.TargetSchemaEnablerForLink;

@ExtendWith(MockitoExtension.class)
public class TargetSchemaEnablerForLinkFactoryTest {
    @Mock
    Scope scope;

    @InjectMocks
    private TargetSchemaEnablerForLinkFactory targetSchemaEnablerForLinkFactory;

    @Test
    public void testProvide() {
        final TargetSchemaEnablerForLink targetSchemaEnablerForLink = targetSchemaEnablerForLinkFactory.provide();
        assertThat(targetSchemaEnablerForLink.test(scope)).isTrue();
    }
}
