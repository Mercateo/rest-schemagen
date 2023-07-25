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
package com.mercateo.common.rest.schemagen.link.helper;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.node.TextNode;
import com.mercateo.common.rest.schemagen.link.LinkCreator;

import jakarta.ws.rs.core.Link;

public class JsonLinkTest {

	public static final String SCHEMA_VALUE = "{\"type\":\"schema\"}";

	public static final String TARGET_SCHEMA_VALUE = "{\"type\":\"targetSchema\"}";

	private JsonLink jsonLink;

	private String uriString;

	private JsonLink jsonLinkTemplate;

	private String uriStringTemplate;

	@BeforeEach
	public void setUp() throws IOException {
		uriString = "https://localhost:1234/base?parm1=val1&parm2=val2#test";
		jsonLink = new JsonLink(Link //
				.fromUri(uriString) //
				.param(LinkCreator.SCHEMA_PARAM_KEY, SCHEMA_VALUE) //
				.param(LinkCreator.TARGET_SCHEMA_PARAM_KEY, TARGET_SCHEMA_VALUE) //
				.param("foo", "bar") //
				.build());

		uriStringTemplate = "https://localhost:1234/{id}/base?parm1=val1&parm2=val2#test";
				jsonLinkTemplate = new JsonLink(Link //
								.fromUri(uriStringTemplate) //
								.param(LinkCreator.SCHEMA_PARAM_KEY, SCHEMA_VALUE) //
								.param(LinkCreator.TARGET_SCHEMA_PARAM_KEY, TARGET_SCHEMA_VALUE) //
								.param("foo", "bar") //
								.build("{id}"));
	}

	@Test
	public void testGetHref() {
		assertThat(jsonLink.getHref()).isEqualTo(uriString);
	}

	@Test
	public void testTemplate() {
			assertThat(jsonLinkTemplate.getHref()).isEqualTo(uriStringTemplate);
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
		jsonLink.setMap("eins", "zwei");
		assertThat(jsonLink.getMap()).containsKey("eins");
	}

	@Test
	public void testWithoutSchemaValues() throws IOException {
		jsonLink = new JsonLink(Link.fromUri(uriString).build());

		assertThat(jsonLink.getSchema()).isNull();
		assertThat(jsonLink.getTargetSchema()).isNull();
	}

}
