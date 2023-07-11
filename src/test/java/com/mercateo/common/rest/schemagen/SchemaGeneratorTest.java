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
