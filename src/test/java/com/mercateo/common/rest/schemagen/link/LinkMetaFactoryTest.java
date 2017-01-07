package com.mercateo.common.rest.schemagen.link;

import com.mercateo.common.rest.schemagen.JsonSchemaGenerator;
import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;
import com.mercateo.common.rest.schemagen.plugin.MethodCheckerForLink;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class LinkMetaFactoryTest {

    @Mock
    private JsonSchemaGenerator schemaGenerator;

    @Mock
    private LinkFactoryContext linkFactoryContext;

    @Test
    public void shouldCreateDeprecatedFactory() throws Exception {
        final LinkMetaFactory linkMetaFactory = LinkMetaFactory.create(schemaGenerator, linkFactoryContext);

        assertThat(linkMetaFactory.getSchemaGenerator()).isEqualTo(schemaGenerator);
        assertThat(linkMetaFactory.getFactoryContext()).isEqualTo(linkFactoryContext);
    }

    @Test
    public void shouldCreateFactory() throws Exception {
        final LinkMetaFactory linkMetaFactory = LinkMetaFactory.create(schemaGenerator);

        assertThat(linkMetaFactory.getSchemaGenerator()).isEqualTo(schemaGenerator);
        assertThat(linkMetaFactory.getFactoryContext()).isNull();
        assertThat(linkMetaFactory.externalLinkFactory()).isNotNull();
    }

    @Test
    public void shouldCreateFactoryFromRequestScopedParts() throws Exception {
        final URI baseUri = new URI("http://host/path");
        final FieldCheckerForSchema fieldCheckerForSchema = mock(FieldCheckerForSchema.class);
        final MethodCheckerForLink methodCheckerForLink = mock(MethodCheckerForLink.class);
        final LinkMetaFactory linkMetaFactory = LinkMetaFactory.create(schemaGenerator, baseUri, methodCheckerForLink, fieldCheckerForSchema);

        assertThat(linkMetaFactory.getSchemaGenerator()).isEqualTo(schemaGenerator);
        final LinkFactoryContext factoryContext = linkMetaFactory.getFactoryContext();
        assertThat(factoryContext).isNotNull();
        assertThat(factoryContext.getBaseUri()).isEqualTo(baseUri);
        assertThat(factoryContext.getMethodCheckerForLink()).isEqualTo(methodCheckerForLink);
        assertThat(factoryContext.getFieldCheckerForSchema()).isEqualTo(fieldCheckerForSchema);
        assertThat(linkMetaFactory.externalLinkFactory()).isNotNull();
    }

    @Test
    public void insecureFactoryForTest() throws Exception {
        final LinkMetaFactory linkMetaFactory = LinkMetaFactory.createInsecureFactoryForTest();

        assertThat(linkMetaFactory.getSchemaGenerator()).isNotNull();
        assertThat(linkMetaFactory.getFactoryContext()).isNotNull();
        assertThat(linkMetaFactory.externalLinkFactory()).isNotNull();
    }
}