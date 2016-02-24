package com.mercateo.common.rest.schemagen.link;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.Link.Builder;

import com.mercateo.common.rest.schemagen.link.injection.BaseUri;
import com.mercateo.common.rest.schemagen.link.relation.RelType;
import com.mercateo.common.rest.schemagen.link.relation.Relation;

/**
 * This LinkFactory is for linking to external URIs. The starting point here is
 * very simple. In the future we may prefix relative URLs with the
 * {@link BaseUri} or check access rights, etc.
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
		if (schemaForLink.isPresent()) {
			linkBuilder.param(LinkCreator.SCHEMA_PARAM_KEY, schemaForLink.get());
		}		
		return linkBuilder.build();
	}
}
