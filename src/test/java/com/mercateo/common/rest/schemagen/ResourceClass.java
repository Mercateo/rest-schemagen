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

import java.util.List;
import java.util.Optional;

import com.mercateo.common.rest.schemagen.link.Entry;
import com.mercateo.common.rest.schemagen.link.LinkMetaFactory;
import com.mercateo.common.rest.schemagen.link.LinkProperties;
import com.mercateo.common.rest.schemagen.link.relation.Rel;
import com.mercateo.common.rest.schemagen.types.ObjectWithSchema;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.MediaType;

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
	@Produces(MediaType.APPLICATION_JSON)
	public ObjectWithSchema<Something> getSomething(@PathParam("id") String id) {
		Optional<Link> link = linkMetaFactory.createFactoryFor(ResourceClass.class).forCall(Rel.SELF,
				r -> r.getSomething(id));

		return ObjectWithSchema.create(new Something(), JsonHyperSchema.from(link));
	}

	@Path("/method/value")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ObjectWithSchema<Void> getWithQuery(@QueryParam("test") String test) {

		return ObjectWithSchema.create(null, null);
	}

	@Path("/methods/{id}")
	@GET
	@RolesAllowed("test")
	public ObjectWithSchema<Something[]> getSomethingArray(@PathParam("id") String id) {
		Optional<Link> link = linkMetaFactory.createFactoryFor(ResourceClass.class).forCall(Rel.SELF,
				r -> r.getSomething(id));

		return ObjectWithSchema.create(new Something[0], JsonHyperSchema.from(link));
	}

	@Path("/method")
	@POST
	@LinkProperties(@Entry(key = "testKey", value = "testValue"))
	public void postSomething(Something something) {
		// nothing
	}

	@Path("/method/{id}")
	@POST
	public void postSomethingWithId(@PathParam("id") String id, @QueryParam("limit") int limit) {
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

	public void noHttpMethod() {
	}

    @GET
    public void multipleQueryParameters(@QueryParam("test") List<String> parameters) {
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
			Optional<Link> link = linkMetaFactory.createFactoryFor(ResourceClass.class)
					.subResource(ResourceClass::getChildResource, ChildResourceClass.class)
					.forCall(Rel.SELF, r -> r.getSomething("12"));

			return ObjectWithSchema.create(new Something[0], JsonHyperSchema.from(link));
		}

		@Path("/subsomething")
		@GET
		@RolesAllowed("test")
		public ObjectWithSchema<Something> getSomething(@PathParam("id") String id) {
			Optional<Link> link = linkMetaFactory.createFactoryFor(ResourceClass.class).forCall(Rel.SELF,
					r -> r.getSomething(id));

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
