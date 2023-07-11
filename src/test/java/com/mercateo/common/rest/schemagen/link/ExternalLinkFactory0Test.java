package com.mercateo.common.rest.schemagen.link;

import jakarta.ws.rs.core.Link;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ExternalLinkFactory0Test {
	private ExternalLinkFactory uut = new ExternalLinkFactory();

	@Test
	public void testCreationWithSchema() throws URISyntaxException {
		URI uri = new URI("http://www.test.com");
		Optional<String> schemaForLink = Optional.of("schema");
		String relName = "relation";
		Link link = uut.createFor(uri, schemaForLink, relName);
		assertEquals(uri, link.getUri());
		assertEquals("_blank", link.getParams().get("target"));
		assertEquals("schema", link.getParams().get("schema"));
		assertEquals("relation", link.getRel());
	}

	@Test
	public void testCreationWithOutSchema() throws URISyntaxException {
		URI uri = new URI("http://www.test.com");
		Optional<String> schemaForLink = Optional.empty();
		String relName = "relation";
		Link link = uut.createFor(uri, schemaForLink, relName);
		assertEquals(uri, link.getUri());
		assertEquals("_blank", link.getParams().get("target"));
		assertNull(link.getParams().get("schema"));
		assertEquals("relation", link.getRel());
	}
}
