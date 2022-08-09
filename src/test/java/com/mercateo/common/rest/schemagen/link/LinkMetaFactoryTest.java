package com.mercateo.common.rest.schemagen.link;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.mercateo.common.rest.schemagen.JsonSchemaGenerator;
import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;
import com.mercateo.common.rest.schemagen.plugin.MethodCheckerForLink;
import com.mercateo.common.rest.schemagen.plugin.TargetSchemaEnablerForLink;

import java.net.URI;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LinkMetaFactoryTest {

    @Mock
    private JsonSchemaGenerator schemaGenerator;

    @Mock
    private LinkFactoryContext linkFactoryContext;

    @Mock
    private TargetSchemaEnablerForLink targetSchemaEnablerForLink;

    @Test
    public void shouldCreateDeprecatedFactory() {
        final LinkMetaFactory linkMetaFactory = LinkMetaFactory.create(schemaGenerator, linkFactoryContext);

        assertThat(linkMetaFactory.getSchemaGenerator()).isEqualTo(schemaGenerator);
        assertThat(linkMetaFactory.getFactoryContext()).isEqualTo(linkFactoryContext);
    }

    @Test
    public void shouldCreateFactory() {
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
        final LinkMetaFactory linkMetaFactory = LinkMetaFactory
                .create(schemaGenerator, baseUri, methodCheckerForLink, fieldCheckerForSchema,
                        targetSchemaEnablerForLink);

        assertThat(linkMetaFactory.getSchemaGenerator()).isEqualTo(schemaGenerator);
        final LinkFactoryContext factoryContext = linkMetaFactory.getFactoryContext();
        assertThat(factoryContext).isNotNull();
        assertThat(factoryContext.getBaseUri()).isEqualTo(baseUri);
        assertThat(factoryContext.getMethodCheckerForLink()).isEqualTo(methodCheckerForLink);
        assertThat(factoryContext.getFieldCheckerForSchema()).isEqualTo(fieldCheckerForSchema);
        assertThat(factoryContext.getTargetSchemaEnablerForLink()).isEqualTo(targetSchemaEnablerForLink);
        assertThat(linkMetaFactory.externalLinkFactory()).isNotNull();
    }

    @Test
    public void insecureFactoryForTest() {
        final LinkMetaFactory linkMetaFactory = LinkMetaFactory.createInsecureFactoryForTest();

        assertThat(linkMetaFactory.getSchemaGenerator()).isNotNull();
        assertThat(linkMetaFactory.getFactoryContext()).isNotNull();
        assertThat(linkMetaFactory.externalLinkFactory()).isNotNull();
    }
}