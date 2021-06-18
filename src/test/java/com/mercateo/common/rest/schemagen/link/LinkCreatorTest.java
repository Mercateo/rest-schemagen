package com.mercateo.common.rest.schemagen.link;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.mercateo.common.rest.schemagen.GenericResource;
import com.mercateo.common.rest.schemagen.ImplementedBeanParamType;
import com.mercateo.common.rest.schemagen.JsonSchemaGenerator;
import com.mercateo.common.rest.schemagen.ResourceClass;
import com.mercateo.common.rest.schemagen.Something;
import com.mercateo.common.rest.schemagen.link.relation.Rel;
import com.mercateo.common.rest.schemagen.link.relation.Relation;

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
import org.mockito.Mockito;

public class LinkCreatorTest {

    private final String testSchema = "test";

    @Test
    public void testGET() throws NoSuchMethodException, SecurityException {
        Link link = createFor(ResourceClass.class, ResourceClass.class.getMethod("getSomething",
                String.class), Relation.of(Rel.SELF), true, "12");

        assertEquals("http://host/base/resource/method/12", link.getUri().toString());
        assertEquals("GET", link.getParams().get("method"));
        assertEquals(testSchema, link.getParams().get("targetSchema"));
    }

    @Test
    public void testGETTargetSchemaDisabled() throws NoSuchMethodException, SecurityException {
        Link link = createFor(ResourceClass.class, ResourceClass.class.getMethod("getSomething",
                String.class), Relation.of(Rel.SELF), false, "12");

        assertEquals("http://host/base/resource/method/12", link.getUri().toString());
        assertEquals("GET", link.getParams().get("method"));
        assertNull(link.getParams().get(LinkCreator.TARGET_SCHEMA_PARAM_KEY));
    }

    @Test
    public void testWithoutBasePath() throws NoSuchMethodException, SecurityException {
        final CallScope callScope = new CallScope(ResourceClass.class, ResourceClass.class
                .getMethod("getSomething", String.class), new Object[] { "12" }, null);

        final JsonSchemaGenerator jsonSchemaGenerator = createJsonSchemaGenerator(true);

        final LinkFactoryContext linkFactoryContext = new LinkFactoryContextDefault(null, o -> true,
                (o, c) -> true, scope -> true);
        final LinkCreator linkCreator = new LinkCreator(jsonSchemaGenerator, linkFactoryContext);

        final Link link = linkCreator.createFor(Collections.singletonList(callScope), Relation.of(
                Rel.SELF));

        assertEquals("resource/method/12", link.getUri().toString());
        assertEquals("GET", link.getParams().get("method"));
        assertEquals(testSchema, link.getParams().get("targetSchema"));
    }

    @Test
    public void testPOST() throws NoSuchMethodException, SecurityException {
        Link link = createFor(ResourceClass.class, ResourceClass.class.getMethod("postSomething",
                Something.class), Relation.of(Rel.SELF), true);

        assertEquals("http://host/base/resource/method", link.getUri().toString());
        assertEquals("POST", link.getParams().get("method"));
        assertEquals(testSchema, link.getParams().get("schema"));
        assertThat(link.getParams().get("testKey")).isEqualTo("testValue");
    }

    @Test
    public void testPOSTWithBaseURI() throws NoSuchMethodException, SecurityException {
        Link link = createFor(ResourceClass.class, ResourceClass.class.getMethod("postSomething",
                Something.class), Relation.of(Rel.SELF), true);

        assertEquals("http://host/base/resource/method", link.getUri().toString());
        assertEquals("POST", link.getParams().get("method"));
        assertEquals(testSchema, link.getParams().get("schema"));
    }

    @Test
    public void failsIfHttpMethodIsMissing() throws NoSuchMethodException, SecurityException {
        assertThatThrownBy(() -> createFor(ResourceClass.class, ResourceClass.class.getMethod(
                "noHttpMethod"), Relation.of(Rel.SELF), true)) //
                .isInstanceOf(IllegalArgumentException.class) //
                        .hasMessage(
                                "LinkCreator: The method has to be annotated with one of: @GET, @POST, @PUT, @DELETE");
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
        Scope scope = new CallScope(ImplementedGenricResource.class, ImplementedGenricResource.class
                .getMethod("get", Object.class), new Object[] { implementedBeanParamType }, null);

        Link link = createFor(scope, Relation.of(Rel.SELF), true);

        assertEquals("http://host/base/test/path?qp2=v2&qp1=v1", link.getUri().toString());
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

        Scope scope = new CallScope(ImplementedGenricResource.class, ImplementedGenricResource.class
                .getMethod("get", String.class), new String[] { "foo" }, null);

        Link link = createFor(scope, Relation.of(Rel.SELF), true);

        assertEquals("http://host/base/test/foo", link.getUri().toString());
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

        Scope scope = new CallScope(ImplementedGenricResource.class, ImplementedGenricResource.class
                .getMethod("get", Object.class), new Object[] { implementedBeanParamType }, null);

        Link link = createFor(scope, Relation.of(Rel.SELF), true);

        assertEquals("http://host/base/test/path", link.getUri().toString());
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
        Scope scope = new CallScope(ImplementedGenericResource.class,
                ImplementedGenericResource.class.getMethod("get", Object.class), new Object[] {
                        implementedBeanParamType }, null);

        Link link = createFor(scope, Relation.of(Rel.SELF), true);

        assertEquals("http://host/base/test/path?elements=foo&elements=bar&elements=baz", link
                .getUri().toString());
        assertEquals("GET", link.getParams().get("method"));
        assertEquals("application/json", link.getParams().get("mediaType"));
    }

    @Test
    public void testTargetSchemaPresentOnMatchingMediaTypeAtMethodLevelTargetSchemaEnabled()
            throws NoSuchMethodException, SecurityException {
        @Path("test")
        class TestResource {
            @GET
            @Produces("application/json")
            public String get() {
                return "test";
            }
        }

        Scope scope = new CallScope(TestResource.class, TestResource.class.getMethod("get"),
                new String[] {}, null);

        Link link = createFor(scope, Relation.of(Rel.SELF), true);

        assertEquals("http://host/base/test", link.getUri().toString());
        assertEquals("GET", link.getParams().get("method"));
        assertEquals("application/json", link.getParams().get("mediaType"));
        assertEquals(testSchema, link.getParams().get(LinkCreator.TARGET_SCHEMA_PARAM_KEY));
    }

    @Test
    public void testTargetSchemaAbsentOnMatchingMediaTypeAtMethodLevelTargetSchemaDisabled()
            throws NoSuchMethodException, SecurityException {
        @Path("test")
        class TestResource {
            @GET
            @Produces("application/json")
            public String get() {
                return "test";
            }
        }

        Scope scope = new CallScope(TestResource.class, TestResource.class.getMethod("get"),
                new String[] {}, null);

        Link link = createFor(scope, Relation.of(Rel.SELF), false);

        assertEquals("http://host/base/test", link.getUri().toString());
        assertEquals("GET", link.getParams().get("method"));
        assertEquals("application/json", link.getParams().get("mediaType"));
        assertNull(link.getParams().get(LinkCreator.TARGET_SCHEMA_PARAM_KEY));
    }

    @Test
    public void testTargetSchemaPresentOnMatchingMediaTypeAtTypeLevelTargetSchemaEnabled()
            throws NoSuchMethodException, SecurityException {
        @Path("test")
        @Produces("application/json")
        class TestResource {
            @GET
            public String get() {
                return "test";
            }
        }

        Scope scope = new CallScope(TestResource.class, TestResource.class.getMethod("get"),
                new String[] {}, null);

        Link link = createFor(scope, Relation.of(Rel.SELF), true);

        assertEquals("http://host/base/test", link.getUri().toString());
        assertEquals("GET", link.getParams().get("method"));
        assertEquals("application/json", link.getParams().get("mediaType"));
        assertEquals(testSchema, link.getParams().get(LinkCreator.TARGET_SCHEMA_PARAM_KEY));
    }

    @Test
    public void testTargetSchemaAbsentOnMatchingMediaTypeAtTypeLevelTargetSchemaDisabled()
            throws NoSuchMethodException, SecurityException {
        @Path("test")
        @Produces("application/json")
        class TestResource {
            @GET
            public String get() {
                return "test";
            }
        }

        Scope scope = new CallScope(TestResource.class, TestResource.class.getMethod("get"),
                new String[] {}, null);

        Link link = createFor(scope, Relation.of(Rel.SELF), false);

        assertEquals("http://host/base/test", link.getUri().toString());
        assertEquals("GET", link.getParams().get("method"));
        assertEquals("application/json", link.getParams().get("mediaType"));
        assertNull(link.getParams().get(LinkCreator.TARGET_SCHEMA_PARAM_KEY));
    }

    @Test
    public void testTargetSchemaAbsentOnNonMatchingMediaType() throws NoSuchMethodException,
            SecurityException {
        @Path("test")
        @Produces("application/octet-stream")
        class TestResource {
            @GET
            public String get() {
                return "test";
            }
        }

        Scope scope = new CallScope(TestResource.class, TestResource.class.getMethod("get"),
                new String[] {}, null);

        Link link = createFor(scope, Relation.of(Rel.SELF), true);

        assertEquals("http://host/base/test", link.getUri().toString());
        assertEquals("GET", link.getParams().get("method"));
        assertEquals("application/octet-stream", link.getParams().get("mediaType"));
        assertNull(link.getParams().get(LinkCreator.TARGET_SCHEMA_PARAM_KEY));
    }

    private JsonSchemaGenerator createJsonSchemaGenerator(boolean targetSchemaEnabledForLink) {
        JsonSchemaGenerator jsonSchemaGenerator = Mockito.mock(JsonSchemaGenerator.class);
        when(jsonSchemaGenerator.createInputSchema(any(), any())).thenReturn(
                Optional.of(testSchema));
        if (targetSchemaEnabledForLink) {
            when(jsonSchemaGenerator.createOutputSchema(any(), any(), any())).thenReturn(
                    Optional.of(testSchema));
        } else {
            when(jsonSchemaGenerator.createOutputSchema(any(), any(), any())).thenReturn(
                    Optional.empty());
        }
        return jsonSchemaGenerator;
    }

    private Link createFor(Class<?> invokedClass, Method method, Relation relation, boolean targetSchemaEnabledForLink,
            Object... params) {
        return createFor(new CallScope(invokedClass, method, params, null), relation, targetSchemaEnabledForLink);
    }

    private Link createFor(Scope method, Relation relation, boolean targetSchemaEnabledForLink) {
        final JsonSchemaGenerator jsonSchemaGenerator = createJsonSchemaGenerator(targetSchemaEnabledForLink);

        final LinkFactoryContext linkFactoryContext = new LinkFactoryContextDefault(URI.create(
                "http://host/base/"), o -> true, (o, c) -> true, scope -> targetSchemaEnabledForLink);
        final LinkCreator linkCreator = new LinkCreator(jsonSchemaGenerator, null);

        return linkCreator.createFor(Collections.singletonList(method), relation,
                linkFactoryContext);
    }

}