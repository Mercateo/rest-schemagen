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
package com.mercateo.common.rest.schemagen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mercateo.common.rest.schemagen.link.LinkFactory;
import com.mercateo.common.rest.schemagen.link.SchemaGenerator;
import com.mercateo.common.rest.schemagen.link.helper.MethodInvocation;
import com.mercateo.common.rest.schemagen.link.relation.Rel;

import jakarta.ws.rs.core.Link;

@ExtendWith(MockitoExtension.class)
public class SchemaGeneratorTest {
    public static class Resource implements JerseyResource {
        @SuppressWarnings("unused")
        public void put(int id, int value) {
            // for test
        }
    }

    public static class SecondResource implements JerseyResource {
        @SuppressWarnings("unused")
        public int get(int id) {
            return 0;
        }
    }

    @Mock
    private LinkFactory<Resource> linkFactoryForResource;

    @Mock
    private LinkFactory<SecondResource> linkFactoryForSecondResource;

    @SuppressWarnings({ "unchecked" })
    @Test
    public void createsLinksForDifferentResources() throws Exception {
        int id = 1;
        int value = 2;
        final MethodInvocation<Resource> putMethodInvocation = r -> r.put(id, value);
        final MethodInvocation<SecondResource> getMethodInvocation = r -> r.get(id);
        final Optional<Link> putLink = Optional.of(Link.fromPath("put").build());
        final Optional<Link> getLink = Optional.of(Link.fromPath("get").build());
        when(linkFactoryForResource.forCall(Rel.UPDATE, putMethodInvocation)).thenReturn(putLink);
        when(linkFactoryForSecondResource.forCall(Rel.CANONICAL, getMethodInvocation)).thenReturn(
                getLink);
        assertThat(SchemaGenerator.<Resource> builder(linkFactoryForResource)//
                .withLink(Rel.UPDATE, putMethodInvocation)
                .withFactory(linkFactoryForSecondResource)
                .withLink(Rel.CANONICAL, getMethodInvocation)
                .build())//
                        .containsExactly(putLink, getLink);
    }
}
