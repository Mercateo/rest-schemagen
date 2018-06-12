/**
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Link;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.mercateo.common.rest.schemagen.link.LinkCreator;

/**
 * this class is needed, since {@link javax.ws.rs.core.Link} could not be
 * serialized properly at the moment
 * 
 * @author joerg.adler
 *
 */
@JsonInclude(Include.NON_NULL)
public class JsonLink {

	private String href;

	private Map<String, String> map = new HashMap<>();

	private JsonNode schema;

	private JsonNode targetSchema;

	public JsonLink(Link link) throws JsonProcessingException, IOException {
		href = getHref(link);
		map = new HashMap<>(link.getParams());
		ObjectMapper mapper = new ObjectMapper();
		String schemaString = link.getParams().get(LinkCreator.SCHEMA_PARAM_KEY);
		if (schemaString != null) {
			schema = mapper.readTree(schemaString);
			map.remove(LinkCreator.SCHEMA_PARAM_KEY);
		}
		String targetSchemaString = link.getParams().get(LinkCreator.TARGET_SCHEMA_PARAM_KEY);
		if (targetSchemaString != null) {
			targetSchema = mapper.readTree(targetSchemaString);
			map.remove(LinkCreator.TARGET_SCHEMA_PARAM_KEY);
		}

	}

	@VisibleForTesting
	String getHref(Link link) {
		String rawPath = link.getUri().getRawPath();
		String uriString = link.getUri().toString();

		// hack for templates
		if (rawPath.contains("%7B")) {
			try {
				return URLDecoder.decode(uriString, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// UTF-8 is supported
				throw new RuntimeException(e);
			}
		}
		return uriString;
	}

	public JsonLink() {

	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	@JsonAnyGetter
	public Map<String, String> getMap() {
		return map;
	}

	@JsonAnySetter
	public void setMap(String key, String value) {
		map.put(key, value);
	}

	public JsonNode getSchema() {
		return schema;
	}

	public void setSchema(JsonNode schema) {
		this.schema = schema;
	}

	public JsonNode getTargetSchema() {
		return targetSchema;
	}

	public void setTargetSchema(JsonNode targetSchema) {
		this.targetSchema = targetSchema;
	}

}
