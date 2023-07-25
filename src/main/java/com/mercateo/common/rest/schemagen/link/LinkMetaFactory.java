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

import static java.util.Objects.requireNonNull;

import java.net.URI;
import java.util.ArrayList;

import com.google.common.annotations.VisibleForTesting;
import com.mercateo.common.rest.schemagen.JerseyResource;
import com.mercateo.common.rest.schemagen.JsonSchemaGenerator;
import com.mercateo.common.rest.schemagen.RestJsonSchemaGenerator;
import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;
import com.mercateo.common.rest.schemagen.plugin.MethodCheckerForLink;
import com.mercateo.common.rest.schemagen.plugin.TargetSchemaEnablerForLink;

public class LinkMetaFactory {

	private static final ExternalLinkFactory externalLinkFactory = new ExternalLinkFactory();

	private final JsonSchemaGenerator schemaGenerator;

	private final LinkFactoryContext linkFactoryContext;

	private LinkMetaFactory(JsonSchemaGenerator schemaGenerator) {
		this(schemaGenerator, null);
	}

	private LinkMetaFactory(JsonSchemaGenerator schemaGenerator, LinkFactoryContext linkFactoryContext) {
		this.schemaGenerator = requireNonNull(schemaGenerator);
		this.linkFactoryContext = linkFactoryContext;
	}

	/**
	 *
	 * @param jsonSchemaGenerator schema generator to be used
	 * @return
	 */
	public static LinkMetaFactory create(JsonSchemaGenerator jsonSchemaGenerator) {
		return new LinkMetaFactory(jsonSchemaGenerator);
	}

	/**
	 *
	 * @param jsonSchemaGenerator
	 * @param linkFactoryContext
	 *
	 * @deprecated please use {@link #create(JsonSchemaGenerator)} instead
	 * @return
	 */
	@Deprecated
	public static LinkMetaFactory create(JsonSchemaGenerator jsonSchemaGenerator, LinkFactoryContext linkFactoryContext) {
		return new LinkMetaFactory(jsonSchemaGenerator, linkFactoryContext);
	}

	/**
	 *
	 * @param jsonSchemaGenerator
	 * @param baseUri
	 * @param methodCheckerForLink
	 * @param fieldCheckerForSchema
	 *
	 * @param targetSchemaEnablerForLink
	 * @deprecated please use {@link #create(JsonSchemaGenerator)} instead
	 * @return
	 */
	@Deprecated
	public static LinkMetaFactory create(JsonSchemaGenerator jsonSchemaGenerator, URI baseUri,
			MethodCheckerForLink methodCheckerForLink, FieldCheckerForSchema fieldCheckerForSchema,
			TargetSchemaEnablerForLink targetSchemaEnablerForLink) {

		requireNonNull(baseUri);
		requireNonNull(methodCheckerForLink);
		requireNonNull(fieldCheckerForSchema);
		return new LinkMetaFactory(jsonSchemaGenerator, new LinkFactoryContextDefault(baseUri, methodCheckerForLink, fieldCheckerForSchema,
				targetSchemaEnablerForLink));
	}

	/**
	 * @deprecated should not be used any more, because the use of a global link factory context is deprecated
	 */
	@Deprecated
	@VisibleForTesting
	public static LinkMetaFactory createInsecureFactoryForTest() {
		return new LinkMetaFactory(new RestJsonSchemaGenerator(), new LinkFactoryContextDefault(URI.create(""), r -> true, (r, c) -> true,
				scope -> true));
	}

	public <T extends JerseyResource> LinkFactory<T> createFactoryFor(Class<T> resourceClass) {
		return new LinkFactory<>(resourceClass, schemaGenerator, linkFactoryContext, new ArrayList<>());
	}

	public LinkFactoryContext getFactoryContext() {
		return linkFactoryContext;
	}

	public ExternalLinkFactory externalLinkFactory() {
		return externalLinkFactory;
	}

    public JsonSchemaGenerator getSchemaGenerator() {
        return schemaGenerator;
    }
}
