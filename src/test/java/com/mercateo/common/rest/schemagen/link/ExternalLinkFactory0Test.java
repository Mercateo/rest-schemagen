package com.mercateo.common.rest.schemagen.link;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.ws.rs.core.Link;

import org.junit.Assert;
import org.junit.Test;

public class ExternalLinkFactory0Test {
	private ExternalLinkFactory uut = new ExternalLinkFactory();

	@Test
	public void testCreationWithSchema() throws URISyntaxException {
		URI uri = new URI("http://www.test.com");
		Optional<String> schemaForLink = Optional.of("schema");
		String relName = "relation";
		Link link = uut.createFor(uri, schemaForLink, relName);
		Assert.assertEquals(uri, link.getUri());
		Assert.assertEquals("_blank", link.getParams().get("target"));
		Assert.assertEquals("schema", link.getParams().get("schema"));
		Assert.assertEquals("relation", link.getRel());
	}

	@Test
	public void testCreationWithOutSchema() throws URISyntaxException {
		URI uri = new URI("http://www.test.com");
		Optional<String> schemaForLink = Optional.empty();
		String relName = "relation";
		Link link = uut.createFor(uri, schemaForLink, relName);
		Assert.assertEquals(uri, link.getUri());
		Assert.assertEquals("_blank", link.getParams().get("target"));
		Assert.assertNull(link.getParams().get("schema"));
		Assert.assertEquals("relation", link.getRel());
	}
}
