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
package com.mercateo.common.rest.schemagen.link;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.Link;

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
