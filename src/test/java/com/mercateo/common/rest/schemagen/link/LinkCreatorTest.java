package com.mercateo.common.rest.schemagen.link;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collections;
import java.util.Optional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Link;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.mercateo.common.rest.schemagen.GenericResource;
import com.mercateo.common.rest.schemagen.ImplementedBeanParamType;
import com.mercateo.common.rest.schemagen.JsonSchemaGenerator;
import com.mercateo.common.rest.schemagen.ResourceClass;
import com.mercateo.common.rest.schemagen.Something;
import com.mercateo.common.rest.schemagen.link.relation.Rel;
import com.mercateo.common.rest.schemagen.link.relation.Relation;

public class LinkCreatorTest {

    private final String testSchema = "test";

    @Test
    public void testGET() throws NoSuchMethodException, SecurityException {
        Link link = createFor(ResourceClass.class, ResourceClass.class.getMethod("getSomething",
                String.class), Relation.of(Rel.SELF), URI.create(""), "12");

        assertEquals("resource/method/12", link.getUri().toString());
        assertEquals("GET", link.getParams().get("method"));
        assertEquals(testSchema, link.getParams().get("targetSchema"));
    }

    @Test
    public void testPOST() throws NoSuchMethodException, SecurityException {
        Link link = createFor(ResourceClass.class, ResourceClass.class.getMethod("postSomething",
                Something.class), Relation.of(Rel.SELF), URI.create(""));

        assertEquals("resource/method", link.getUri().toString());
        assertEquals("POST", link.getParams().get("method"));
        assertEquals(testSchema, link.getParams().get("schema"));
        assertThat(link.getParams().get("testKey")).isEqualTo("testValue");
    }

    @Test
    public void testPOSTWithBaseURI() throws NoSuchMethodException, SecurityException {
        Link link = createFor(ResourceClass.class, ResourceClass.class.getMethod("postSomething",
                Something.class), Relation.of(Rel.SELF), URI.create("http://localhost:8080/"));

        assertEquals("http://localhost:8080/resource/method", link.getUri().toString());
        assertEquals("POST", link.getParams().get("method"));
        assertEquals(testSchema, link.getParams().get("schema"));
    }

    @Test
    public void failsIfHttpMethodIsMissing() throws NoSuchMethodException, SecurityException {
        assertThatThrownBy(() ->
                createFor(ResourceClass.class, ResourceClass.class.getMethod("noHttpMethod"),
                        Relation.of(Rel.SELF), URI.create("http://localhost:8080/"))) //
                .isInstanceOf(IllegalArgumentException.class) //
                .hasMessage("LinkCreator: The method has to be annotated with one of: @GET, @POST, @PUT, @DELETE");
    }

    @Test
    public void testBeanParams() throws NoSuchMethodException, SecurityException {
        @Path("test")
        class ImplementedGenricResource extends
                GenericResource<Something, ImplementedBeanParamType> {
            @Override
            protected Something getReturnType(ImplementedBeanParamType param) {
                return new Something();
            }
        }

        ImplementedBeanParamType implementedBeanParamType = new ImplementedBeanParamType();
        implementedBeanParamType.setPathParam("path");
        implementedBeanParamType.setQueryParam1("v1");
        implementedBeanParamType.setQueryParam2("v2");
        Scope scope = new CallScope(ImplementedGenricResource.class, ImplementedGenricResource.class.getMethod("get",
                Object.class), new Object[] { implementedBeanParamType }, null);

        Link link = createFor(scope, Relation.of(Rel.SELF), URI.create(
                "http://localhost:8080/"));

        assertEquals("http://localhost:8080/test/path?qp2=v2&qp1=v1", link.getUri().toString());
        assertEquals("GET", link.getParams().get("method"));
    }
    
    @Test
    public void testPathParam() throws NoSuchMethodException, SecurityException {
		@Path("test")
		class ImplementedGenricResource {
			@GET
			@Path("{pathParam1}")
			@Produces("application/json")
			public String get(@PathParam("pathParam1") String param) {
				return "input:" + param;
			}
		}
    	
    	Scope scope = new CallScope(ImplementedGenricResource.class, ImplementedGenricResource.class.getMethod("get",
    			String.class), new String[] { "foo" }, null);
    	
    	Link link = createFor(scope, Relation.of(Rel.SELF), URI.create(
    			"http://localhost:8080/"));
    	
    	assertEquals("http://localhost:8080/test/foo", link.getUri().toString());
    	assertEquals("GET", link.getParams().get("method"));
    }
    
    @Test
    public void testTemplatedPathParam() throws NoSuchMethodException, SecurityException {
    	@Path("test")
    	class ImplementedGenricResource {
    		@GET
    		@Path("{pathParam1}")
    		@Produces("application/json")
    		public String get(@PathParam("pathParam1") String param) {
    			return "input:" + param;
    		}
    	}
    	
    	Scope scope = new CallScope(ImplementedGenricResource.class, ImplementedGenricResource.class.getMethod("get",
    			String.class), new String[] { null }, null);
    	
    	Link link = createFor(scope, Relation.of(Rel.SELF), URI.create(
    			"http://localhost:8080/"));
    	
    	assertEquals("http://localhost:8080/test/%7BpathParam1%7D", link.getUri().toString());
    	assertEquals("GET", link.getParams().get("method"));
    }

    @Test
    public void testBeanParamsWithDefaultValues() throws NoSuchMethodException, SecurityException {

        @Path("test")
        class ImplementedGenricResource extends
                GenericResource<Something, ImplementedBeanParamType> {
            @Override
            protected Something getReturnType(ImplementedBeanParamType param) {
                return new Something();
            }
        }

        ImplementedBeanParamType implementedBeanParamType = new ImplementedBeanParamType();
        implementedBeanParamType.setPathParam("path");

        Scope scope = new CallScope(ImplementedGenricResource.class, ImplementedGenricResource.class.getMethod("get",
                Object.class), new Object[] { implementedBeanParamType }, null );

        Link link = createFor(scope, Relation.of(Rel.SELF), URI.create(
                "http://localhost:8080/"));

        assertEquals("http://localhost:8080/test/path", link.getUri().toString());
        assertEquals("GET", link.getParams().get("method"));
    }

    @Test
    public void testBeanParamWithMultiValueQueryParam() throws NoSuchMethodException,
            SecurityException {

        @Path("test")
        class ImplementedGenericResource extends
                GenericResource<Something, ImplementedBeanParamType> {
            @Override
            protected Something getReturnType(ImplementedBeanParamType param) {
                return new Something();
            }
        }

        ImplementedBeanParamType implementedBeanParamType = new ImplementedBeanParamType();
        implementedBeanParamType.setPathParam("path");
        implementedBeanParamType.setElements("foo", "bar", "baz");
        Scope scope = new CallScope(ImplementedGenericResource.class, ImplementedGenericResource.class.getMethod("get",
                Object.class), new Object[] { implementedBeanParamType }, null);

        Link link = createFor(scope, Relation.of(Rel.SELF), URI.create(
                "http://localhost:8080/"));

        assertEquals("http://localhost:8080/test/path?elements=foo&elements=bar&elements=baz", link
                .getUri().toString());
        assertEquals("GET", link.getParams().get("method"));
        assertEquals("application/json", link.getParams().get("mediaType"));
    }

    private JsonSchemaGenerator createJsonSchemaGenerator() {
        JsonSchemaGenerator jsonSchemaGenerator = Mockito.mock(JsonSchemaGenerator.class);
        when(jsonSchemaGenerator.createInputSchema(Matchers.any(), Matchers.any())).thenReturn(
                Optional.of(testSchema));
        when(jsonSchemaGenerator.createOutputSchema(Matchers.any(), Matchers.any())).thenReturn(
                Optional.of(testSchema));
        return jsonSchemaGenerator;
    }

    private Link createFor(Class<?> invokedClass, Method method, Relation relation, URI baseUri,
            Object... params) {
        return createFor(new CallScope(invokedClass, method, params, null), relation, baseUri);
    }

    private Link createFor(Scope method, Relation relation, URI baseURI) {
        final JsonSchemaGenerator jsonSchemaGenerator = createJsonSchemaGenerator();

        final LinkFactoryContext linkFactoryContext = new LinkFactoryContext(jsonSchemaGenerator,
                baseURI, o -> true, (o, c) -> true);
        final LinkCreator linkCreator = new LinkCreator(linkFactoryContext);

        return linkCreator.createFor(Collections.singletonList(method), relation);
    }

}