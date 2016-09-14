package com.mercateo.common.rest.schemagen.link.helper;

import java.io.IOException;
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
		href = link.getUri().toString();
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
