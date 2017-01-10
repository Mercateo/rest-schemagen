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
	 * @deprecated please use {@link #create(JsonSchemaGenerator)} instead
	 * @return
	 */
	@Deprecated
	public static LinkMetaFactory create(JsonSchemaGenerator jsonSchemaGenerator, URI baseUri,
			MethodCheckerForLink methodCheckerForLink, FieldCheckerForSchema fieldCheckerForSchema) {

		requireNonNull(baseUri);
		requireNonNull(methodCheckerForLink);
		requireNonNull(fieldCheckerForSchema);
		return new LinkMetaFactory(jsonSchemaGenerator, new LinkFactoryContextDefault(baseUri, methodCheckerForLink, fieldCheckerForSchema));
	}

	/**
	 * @deprecated should not be used any more, because the use of a global link factory context is deprecated
	 */
	@Deprecated
	@VisibleForTesting
	public static LinkMetaFactory createInsecureFactoryForTest() {
		return new LinkMetaFactory(new RestJsonSchemaGenerator(), new LinkFactoryContextDefault(URI.create(""), r -> true, (r, c) -> true));
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
