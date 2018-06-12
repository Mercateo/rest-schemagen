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
package com.mercateo.common.rest.schemagen.link;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.Link.Builder;

import com.mercateo.common.rest.schemagen.link.relation.RelType;
import com.mercateo.common.rest.schemagen.link.relation.Relation;

/**
 * This LinkFactory is for linking to external URIs. The starting point here is
 * very simple. In the future we may prefix relative URLs with the
 * baseUri or check access rights, etc.
 * 
 * @author joerg_adler
 *
 */
public class ExternalLinkFactory {
	public Link createFor(URI uri, Optional<String> schemaForLink, String relName) {
		Objects.requireNonNull(uri);
		Objects.requireNonNull(schemaForLink);
		Objects.requireNonNull(relName);

		Relation rel = Relation.of(relName, RelType.OTHER);
		Builder linkBuilder = LinkCreator.setRelation(rel, uri);
		schemaForLink.ifPresent(s -> linkBuilder.param(LinkCreator.SCHEMA_PARAM_KEY, s));
		return linkBuilder.build();
	}
}
