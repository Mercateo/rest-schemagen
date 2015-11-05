package com.mercateo.common.rest.schemagen;

import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;

import com.mercateo.common.rest.schemagen.link.LinkMetaFactory;
import com.mercateo.common.rest.schemagen.link.relation.Rel;
import com.mercateo.common.rest.schemagen.types.ObjectWithSchema;

@Path("resource")
@Produces(MediaType.APPLICATION_JSON)
@SuppressWarnings("unused")
public class ResourceClass implements JerseyResource {

    @Inject
    private LinkMetaFactory linkMetaFactory;

    @Inject
    private ChildResourceClass childResource;

    @Path("/method/{id}")
    @GET

    @RolesAllowed("test")
    public ObjectWithSchema<Something> getSomething(@PathParam("id") String id) {
        Optional<Link> link = linkMetaFactory.createFactoryFor(ResourceClass.class).forCall(
                Rel.SELF, r -> r.getSomething(id));

        return ObjectWithSchema.create(new Something(), JsonHyperSchema.from(link));
    }

    @Path("/methods/{id}")
    @GET
    @RolesAllowed("test")
    public ObjectWithSchema<Something[]> getSomethingArray(@PathParam("id") String id) {
        Optional<Link> link = linkMetaFactory.createFactoryFor(ResourceClass.class).forCall(
                Rel.SELF, r -> r.getSomething(id));

        return ObjectWithSchema.create(new Something[0], JsonHyperSchema.from(link));
    }

    @Path("/method")
    @POST
    public void postSomething(Something something) {
        // nothing
    }

    @Path("/method/{id}")
    @POST
    public void postSomethingWithId(@PathParam("id") String id, Something something) {
        // nothing
    }

    @Path("/sub")
    public ChildResourceClass getChildResource() {
        return childResource;
    }

    @GET
    @Path("{id2}")
    public String getTwoIds(@PathParam("id") String id, @PathParam("id2") String id2) {
        return "in two ids";
    }

    @Path("parentResource")
    public static class ParentResourceClass implements JerseyResource {

        @Inject
        private ResourceClass resourceClass;

        @Path("subresource")
        public ResourceClass getSubResource() {
            return resourceClass;
        }

        @Path("{id}/subresource")
        public ResourceClass getSubResourceForIdInParentResource() {
            return resourceClass;
        }
    }

    public static class ChildResourceClass implements JerseyResource {

        @Inject
        private LinkMetaFactory linkMetaFactory;

        @Path("/submethod/{id}")
        @GET
        @RolesAllowed("test")
        public ObjectWithSchema<Something[]> getSomethingArray(@PathParam("id") String id) {
            Optional<Link> link = linkMetaFactory.createFactoryFor(ResourceClass.class).subResource(
                    ResourceClass::getChildResource, ChildResourceClass.class).forCall(Rel.SELF,
                            r -> r.getSomething("12"));

            return ObjectWithSchema.create(new Something[0], JsonHyperSchema.from(link));
        }

        @Path("/subsomething")
        @GET
        @RolesAllowed("test")
        public ObjectWithSchema<Something> getSomething(@PathParam("id") String id) {
            Optional<Link> link = linkMetaFactory.createFactoryFor(ResourceClass.class).forCall(
                    Rel.SELF, r -> r.getSomething(id));

            return ObjectWithSchema.create(new Something(), JsonHyperSchema.from(link));
        }

        @Path("/submethod")
        @POST
        public void postSomething(Something something) {
            // nothing
        }

        @Path("/submethod/{id}")
        @POST
        public void postSomethingWithId(@PathParam("id") String id, Something something) {
            // nothing
        }
    }
}