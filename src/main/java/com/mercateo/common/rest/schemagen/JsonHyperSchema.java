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

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.VisibleForTesting;
import com.mercateo.common.rest.schemagen.link.LinkCreator;
import com.mercateo.common.rest.schemagen.link.helper.JsonLink;
import com.mercateo.common.rest.schemagen.link.relation.RelationContainer;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.Link;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class JsonHyperSchema {

	@XmlJavaTypeAdapter(JaxbAdapter.class)
	private List<Link> links;

	public JsonHyperSchema(@JsonProperty("links") Collection<Link> links) {
		this(links.stream());
	}

	public JsonHyperSchema(Stream<Link> linkStream) {
		this.links = requireNonNull(linkStream).filter(Objects::nonNull).collect(Collectors.toList());
	}

	public List<Link> getLinks() {
		return links;
	}

	@VisibleForTesting
	public Optional<Link> getByRel(RelationContainer rel) {
		return links.stream().filter(e -> e.getRel().equals(rel.getRelation().getName())).findFirst();
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public static class JaxbAdapter extends XmlAdapter<JsonLink, Link> {
		@Override
		public Link unmarshal(JsonLink v) {
			Link.Builder lb = Link.fromUri(v.getHref());
			if (v.getMap() != null) {
				for (Entry<String, String> en : v.getMap().entrySet()) {
					lb.param(en.getKey(), en.getValue());
				}
			}
			if (v.getSchema() != null) {
				lb.param(LinkCreator.SCHEMA_PARAM_KEY, v.getSchema().toString());
			}

			if (v.getTargetSchema() != null) {
				lb.param(LinkCreator.TARGET_SCHEMA_PARAM_KEY, v.getTargetSchema().toString());
			}
			return lb.build();
		}

		@Override
		public JsonLink marshal(Link v) {
			try {
				return new JsonLink(v);
			} catch (IOException e) {
				throw new ProcessingException(e);
			}
		}
	}

	public static JsonHyperSchema from(Link... links) {
		return new JsonHyperSchema(Arrays.stream(links));
	}

	@SafeVarargs
	public static JsonHyperSchema from(Optional<Link>... links) {
		Stream<Optional<Link>> stream = Arrays.stream(links);
		return new JsonHyperSchema(filterOptionals(stream));
	}

	public static JsonHyperSchema from(Collection<Link> links) {
		return new JsonHyperSchema(links);
	}

	public static JsonHyperSchema fromOptional(Collection<Optional<Link>> links) {
		return new JsonHyperSchema(filterOptionals(links.stream()));
	}

	private static <T> Stream<T> filterOptionals(Stream<Optional<T>> streamWithOptionals) {
		return streamWithOptionals.filter(Objects::nonNull).flatMap(e -> e.map(Stream::of).orElse(Stream.empty()));
	}

	@Override
	public String toString() {
		return "JsonHyperSchema{" +
				"links=" + links +
				'}';
	}
}
