package com.mercateo.common.rest.schemagen.link.injection;

import com.mercateo.common.rest.schemagen.JerseyResource;
import com.mercateo.common.rest.schemagen.link.LinkFactory;
import com.mercateo.common.rest.schemagen.link.LinkMetaFactory;
import com.mercateo.common.rest.schemagen.link.relation.Rel;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Application;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
