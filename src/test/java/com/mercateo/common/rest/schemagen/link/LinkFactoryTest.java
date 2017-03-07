package com.mercateo.common.rest.schemagen.link;

import com.mercateo.common.rest.schemagen.ResourceClass;
import com.mercateo.common.rest.schemagen.ResourceClass.ChildResourceClass;
import com.mercateo.common.rest.schemagen.ResourceClass.ParentResourceClass;
import com.mercateo.common.rest.schemagen.RestJsonSchemaGenerator;
import com.mercateo.common.rest.schemagen.Something;
import com.mercateo.common.rest.schemagen.link.relation.Rel;
import com.mercateo.common.rest.schemagen.link.relation.Relation;
import com.mercateo.common.rest.schemagen.parameter.CallContext;
import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;
import com.mercateo.common.rest.schemagen.plugin.common.RolesAllowedChecker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.SecurityContext;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@SuppressWarnings("boxing")
@RunWith(MockitoJUnitRunner.class)
public class LinkFactoryTest {

    @Mock
    private SecurityContext securityContext;

    @Mock
    private FieldCheckerForSchema fieldCheckerForSchema;

    private LinkFactoryContext linkFactoryContext;

    private LinkMetaFactory linkMetaFactory;

    @Before
    public void setUp() throws URISyntaxException {
        linkFactoryContext = new LinkFactoryContextDefault(new URI("basePath/"), new RolesAllowedChecker(securityContext), fieldCheckerForSchema);

        linkMetaFactory = LinkMetaFactory.create(new RestJsonSchemaGenerator());
    }

    @Test
    public void fieldIsNotInTargetSchema() {
        allowRole("test");
        Link link = linkMetaFactory.createFactoryFor(ResourceClass.class).forCall(Rel.SELF, r -> r
                .getSomething("12"), linkFactoryContext).get();
        assertNull(link.getParams().get("targetSchema"));
    }

    @Test
    public void fieldInTargetSchema() throws NoSuchFieldException, SecurityException {
        linkMetaFactory = LinkMetaFactory.create(new RestJsonSchemaGenerator(), linkFactoryContext);

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
                .postSomething(null), linkFactoryContext).get();
        assertNull(link.getParams().get("schema"));
    }

    @Test
    public void fieldInSchema() throws NoSuchFieldException, SecurityException {
        when(fieldCheckerForSchema.test(eq(Something.class.getDeclaredField("id")), any()))
                .thenReturn(Boolean.TRUE);
        Link link = linkMetaFactory.createFactoryFor(ResourceClass.class).forCall(Rel.SELF, r -> r
                .postSomething(null), linkFactoryContext).get();
        assertEquals("{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"string\"}}}", link
                .getParams().get("schema"));
    }

    @Test
    public void testCorrectLinkGenerationGET() {
        linkMetaFactory = LinkMetaFactory.create(new RestJsonSchemaGenerator(), linkFactoryContext);

        allowRole("test");
        Link link = linkMetaFactory.createFactoryFor(ResourceClass.class).forCall(Rel.SELF, r -> r
                .getSomething("12")).get();
        assertEquals("basePath/resource/method/12", link.getUri().toString());
        assertEquals("GET", link.getParams().get("method"));
    }

    @Test
    public void testCorrectLinkGenerationGETArray() {
        allowRole("test");
        Link link = linkMetaFactory.createFactoryFor(ResourceClass.class).forCall(() -> Relation.of("test"), r -> r
                .getSomethingArray("12"), CallContext.create(), linkFactoryContext).get();
        assertEquals("basePath/resource/methods/12", link.getUri().toString());
        assertEquals("GET", link.getParams().get("method"));
    }

    @Test
    public void testCorrectLinkGenerationPOST() {
        linkMetaFactory = LinkMetaFactory.create(new RestJsonSchemaGenerator(), linkFactoryContext);

        Link link = linkMetaFactory.createFactoryFor(ResourceClass.class).forCall(() -> Relation.of("test"), r -> r
                .postSomething(null)).get();
        assertEquals("basePath/resource/method", link.getUri().toString());
        assertEquals("POST", link.getParams().get("method"));
    }

    @Test
    public void testCorrectLinkGenerationPOSTWithParam() {
        linkMetaFactory = LinkMetaFactory.create(new RestJsonSchemaGenerator(), linkFactoryContext);
        
        Link link = linkMetaFactory.createFactoryFor(ResourceClass.class).forCall(() -> Relation.of("self"), r -> r
                .postSomethingWithId("12", 100)).get();
        assertEquals("basePath/resource/method/12?limit=100", link.getUri().toString());
        assertEquals("POST", link.getParams().get("method"));
    }

    @Test
    public void testCorrectDeprecatedLinkGenerationGETWithRolesNegative() {
        linkMetaFactory = LinkMetaFactory.create(new RestJsonSchemaGenerator(), linkFactoryContext);

        Optional<Link> linkOption = linkMetaFactory.createFactoryFor(ResourceClass.class).forCall(
                Rel.SELF, r -> r.getSomething("12"));

        assertThat(linkOption).isEmpty();
    }

    @Test
    public void testCorrectLinkGenerationGETWithRolesNegative() {
        linkMetaFactory = LinkMetaFactory.create(new RestJsonSchemaGenerator());

        Optional<Link> linkOption = linkMetaFactory.createFactoryFor(ResourceClass.class).forCall(
                Rel.SELF, r -> r.getSomething("12"), linkFactoryContext);

        assertThat(linkOption).isEmpty();
    }

    @Test
    public void testCorrectLinkGenerationGETWithSubResource() {
        allowRole("test");

        Optional<Link> linkOption = linkMetaFactory.createFactoryFor(ParentResourceClass.class)
                .subResource(ParentResourceClass::getSubResource, ResourceClass.class).forCall(
                        Rel.SELF, r -> r.getSomething("12"), linkFactoryContext);

        assertTrue(linkOption.isPresent());
        assertEquals("basePath/parentResource/subresource/method/12", linkOption.get().getUri()
                .toString());
        assertEquals(linkOption.get().getParams().get("method"), "GET");
    }

    @Test
    public void testCorrectLinkGenerationGETWithSubResourceTwoIds() {

        Optional<Link> linkOption = linkMetaFactory.createFactoryFor(ParentResourceClass.class)
                .subResource(ParentResourceClass::getSubResourceForIdInParentResource,
                        ResourceClass.class).forCall(Rel.SELF, r -> r.getTwoIds("1", "2"), linkFactoryContext);

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
                                        .getSomethingArray("14"), linkFactoryContext);

        assertTrue(linkOption.isPresent());
 
        final Link link = linkOption.get();

        assertThat(link.getUri().getPath()).isEqualTo(
                "basePath/parentResource/subresource/sub/submethod/14");
        assertThat(link.getParams()).hasSize(3);
		assertThat(link.getParams())//
				.containsEntry("rel", "self")//
				.containsEntry("method", "GET")//
				.containsEntry("schema", "{\"type\":\"string\"}");
    }

    private void allowRole(String roleName) {
        when(securityContext.isUserInRole(roleName)).thenReturn(true);
    }
}
