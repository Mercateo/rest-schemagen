package com.mercateo.common.rest.schemagen.link;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.SecurityContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mercateo.common.rest.schemagen.ResourceClass;
import com.mercateo.common.rest.schemagen.ResourceClass.ChildResourceClass;
import com.mercateo.common.rest.schemagen.ResourceClass.ParentResourceClass;
import com.mercateo.common.rest.schemagen.RestJsonSchemaGenerator;
import com.mercateo.common.rest.schemagen.Something;
import com.mercateo.common.rest.schemagen.link.relation.Rel;
import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;
import com.mercateo.common.rest.schemagen.plugin.common.RolesAllowedChecker;

@SuppressWarnings("boxing")
@RunWith(MockitoJUnitRunner.class)
public class LinkFactoryTest {

    @Mock
    private SecurityContext securityContext;

    @Mock
    private FieldCheckerForSchema fieldCheckerForSchema;

    private URI baseUri;

    private LinkMetaFactory linkMetaFactory;

    @Before
    public void setUp() throws URISyntaxException {
        baseUri = new URI("basePath/");

        linkMetaFactory = LinkMetaFactory.create(new RestJsonSchemaGenerator(), baseUri,
                new RolesAllowedChecker(securityContext), fieldCheckerForSchema);
    }

    @Test
    public void fieldIsNotInTargetSchema() {
        allowRole("test");
        Link link = linkMetaFactory.createFactoryFor(ResourceClass.class).forCall(Rel.SELF, r -> r
                .getSomething("12")).get();
        assertNull(link.getParams().get("targetSchema"));
    }

    @Test
    public void fieldInTargetSchema() throws NoSuchFieldException, SecurityException {
        allowRole("test");
        when(fieldCheckerForSchema.test(eq(Something.class.getDeclaredField("id")), any()))
                .thenReturn(Boolean.TRUE);
        Link link = linkMetaFactory.createFactoryFor(ResourceClass.class).forCall(Rel.SELF, r -> r
                .getSomething("12")).get();
        assertEquals("{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"string\"}}}", link
                .getParams().get("targetSchema"));
    }

    @Test
    public void fieldIsNotInSchema() {
        Link link = linkMetaFactory.createFactoryFor(ResourceClass.class).forCall(Rel.SELF, r -> r
                .postSomething(null)).get();
        assertNull(link.getParams().get("schema"));
    }

    @Test
    public void fieldInSchema() throws NoSuchFieldException, SecurityException {
        when(fieldCheckerForSchema.test(eq(Something.class.getDeclaredField("id")), any()))
                .thenReturn(Boolean.TRUE);
        Link link = linkMetaFactory.createFactoryFor(ResourceClass.class).forCall(Rel.SELF, r -> r
                .postSomething(null)).get();
        assertEquals("{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"string\"}}}", link
                .getParams().get("schema"));
    }

    @Test
    public void testCorrectLinkGenerationGET() {
        allowRole("test");
        Link link = linkMetaFactory.createFactoryFor(ResourceClass.class).forCall(Rel.SELF, r -> r
                .getSomething("12")).get();
        assertEquals("basePath/resource/method/12", link.getUri().toString());
        assertEquals("GET", link.getParams().get("method"));
    }

    @Test
    public void testCorrectLinkGenerationGETArray() {
        allowRole("test");
        Link link = linkMetaFactory.createFactoryFor(ResourceClass.class).forCall(Rel.SELF, r -> r
                .getSomethingArray("12")).get();
        assertEquals("basePath/resource/methods/12", link.getUri().toString());
        assertEquals("GET", link.getParams().get("method"));
    }

    @Test
    public void testCorrectLinkGenerationPOST() {
        Link link = linkMetaFactory.createFactoryFor(ResourceClass.class).forCall(Rel.SELF, r -> r
                .postSomething(null)).get();
        assertEquals("basePath/resource/method", link.getUri().toString());
        assertEquals("POST", link.getParams().get("method"));
    }

    @Test
    public void testCorrectLinkGenerationPOSTWithParam() {
        Link link = linkMetaFactory.createFactoryFor(ResourceClass.class).forCall(Rel.SELF, r -> r
                .postSomethingWithId("12", 100)).get();
        assertEquals("basePath/resource/method/12?limit=100", link.getUri().toString());
        assertEquals("POST", link.getParams().get("method"));
    }

    @Test
    public void testCorrectLinkGenerationGETWithRolesNegative() {
        Optional<Link> linkOption = linkMetaFactory.createFactoryFor(ResourceClass.class).forCall(
                Rel.SELF, r -> r.getSomething("12"));

        assertFalse(linkOption.isPresent());
    }

    @Test
    public void testCorrectLinkGenerationGETWithSubResource() {
        allowRole("test");

        Optional<Link> linkOption = linkMetaFactory.createFactoryFor(ParentResourceClass.class)
                .subResource(ParentResourceClass::getSubResource, ResourceClass.class).forCall(
                        Rel.SELF, r -> r.getSomething("12"));

        assertTrue(linkOption.isPresent());
        assertEquals("basePath/parentResource/subresource/method/12", linkOption.get().getUri()
                .toString());
        assertEquals(linkOption.get().getParams().get("method"), "GET");
    }

    @Test
    public void testCorrectLinkGenerationGETWithSubResourceTwoIds() {

        Optional<Link> linkOption = linkMetaFactory.createFactoryFor(ParentResourceClass.class)
                .subResource(ParentResourceClass::getSubResourceForIdInParentResource,
                        ResourceClass.class).forCall(Rel.SELF, r -> r.getTwoIds("1", "2"));

        assertTrue(linkOption.isPresent());
        assertEquals("basePath/parentResource/1/subresource/2", linkOption.get().getUri()
                .toString());
        assertEquals(linkOption.get().getParams().get("method"), "GET");
    }

    @Test
    public void testCorrectLinkGenerationWithTwoScopeLevels() {
        allowRole("test");

        final Optional<Link> linkOption = linkMetaFactory.createFactoryFor(
                ParentResourceClass.class).subResource(ParentResourceClass::getSubResource,
                        ResourceClass.class).subResource(ResourceClass::getChildResource,
                                ChildResourceClass.class).forCall(Rel.SELF, r -> r
                                        .getSomethingArray("14"));

        assertTrue(linkOption.isPresent());

        final Link link = linkOption.get();

        assertThat(link.getUri().getPath()).isEqualTo(
                "basePath/parentResource/subresource/sub/submethod/14");
        assertThat(link.getParams()).hasSize(2);
        assertThat(link.getParams()).containsEntry("rel", "self").containsEntry("method", "GET");
    }

    private void allowRole(String test) {
        when(securityContext.isUserInRole(test)).thenReturn(true);
    }
}
