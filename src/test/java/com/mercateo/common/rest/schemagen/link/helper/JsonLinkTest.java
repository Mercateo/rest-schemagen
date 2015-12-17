package com.mercateo.common.rest.schemagen.link.helper;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.HashMap;

import javax.ws.rs.core.Link;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.TextNode;
import com.mercateo.common.rest.schemagen.link.LinkCreator;

public class JsonLinkTest {

    public static final String SCHEMA_VALUE = "{\"type\":\"schema\"}";

    public static final String TARGET_SCHEMA_VALUE = "{\"type\":\"targetSchema\"}";

    private JsonLink jsonLink;

    private String uriString;

    @Before
    public void setUp() throws IOException {
        uriString = "https://localhost:1234/base?parm1=val1&parm2=val2#test";
        jsonLink = new JsonLink(Link //
                .fromUri(uriString) //
                .param(LinkCreator.SCHEMA_PARAM_KEY, SCHEMA_VALUE) //
                .param(LinkCreator.TARGET_SCHEMA_PARAM_KEY, TARGET_SCHEMA_VALUE) //
                .param("foo", "bar") //
                .build());
    }

    @Test
    public void testGetHref() {
        assertThat(jsonLink.getHref()).isEqualTo(uriString);
    }

    @Test
    public void testSetHref() {
        jsonLink.setHref("foo");
        assertThat(jsonLink.getHref()).isEqualTo("foo");
    }

    @Test
    public void testGetSchema() {
        assertThat(jsonLink.getSchema().toString()).isEqualTo(SCHEMA_VALUE);
    }

    @Test
    public void testSetSchema() {
        jsonLink.setSchema(new TextNode("foo"));
        assertThat(jsonLink.getSchema().toString()).isEqualTo("\"foo\"");
    }

    @Test
    public void testGetTargetSchema() {
        assertThat(jsonLink.getTargetSchema().toString()).isEqualTo(TARGET_SCHEMA_VALUE);
    }

    @Test
    public void testSetTargetSchema() {
        jsonLink.setTargetSchema(new TextNode("bar"));
        assertThat(jsonLink.getTargetSchema().toString()).isEqualTo("\"bar\"");
    }

    @Test
    public void testGetMap() {
        assertThat(jsonLink.getMap()).hasSize(1).containsEntry("foo", "bar");
    }

    @Test
    public void testSetMap() {
        jsonLink.setMap(new HashMap<>());
        assertThat(jsonLink.getMap()).isEmpty();
    }

    @Test
    public void testWithoutSchemaValues() throws IOException {
        jsonLink = new JsonLink(Link.fromUri(uriString).build());

        assertThat(jsonLink.getSchema()).isNull();
        assertThat(jsonLink.getTargetSchema()).isNull();
    }

}