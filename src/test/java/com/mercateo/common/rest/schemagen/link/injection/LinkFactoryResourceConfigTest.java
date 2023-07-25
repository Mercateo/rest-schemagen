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

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;

import com.mercateo.common.rest.schemagen.JerseyResource;
import com.mercateo.common.rest.schemagen.link.LinkFactory;
import com.mercateo.common.rest.schemagen.link.LinkMetaFactory;
import com.mercateo.common.rest.schemagen.link.relation.Rel;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Application;

public class LinkFactoryResourceConfigTest extends JerseyTest {

    @Path("hello")
    public static class HelloResource implements JerseyResource {
        @Inject
        private LinkMetaFactory linkMetaFactory;

        @GET
        public String getHello() {
            final LinkFactory<HelloResource> linkFactory = linkMetaFactory.createFactoryFor(HelloResource.class);
            linkFactory.forCall(Rel.SELF, HelloResource::getHello);
            return "Hello World!";
        }
    }

    @Override
    protected Application configure() {
        final ResourceConfig resourceConfig = new ResourceConfig(HelloResource.class);
        LinkFactoryResourceConfig.configure(resourceConfig);
        return resourceConfig;
    }

    @Test
    public void configuredResourceShouldBeSetUpAndWorking() {
        final String hello = target("hello").request().get(String.class);
        assertThat(hello).isEqualTo("Hello World!");
    }
}
