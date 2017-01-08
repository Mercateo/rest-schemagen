package com.mercateo.common.rest.schemagen.link.injection;

import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseUriTest {

    private URI baseUri;

    @Before
    public void setUp() throws Exception {
        baseUri = new URI("http://host/path");
    }

    @Test
    public void shouldReturnContainedUri() throws Exception {
        BaseUri baseUriWrapper = new BaseUri(baseUri);

        assertThat(baseUriWrapper.get()).isEqualTo(baseUri);
    }

    @Test
    public void toStringShouldShowContainedUri() throws Exception {
        BaseUri baseUriWrapper = new BaseUri(baseUri);

        assertThat(baseUriWrapper.toString()).isEqualTo(baseUri.toString());
    }

    @Test
    public void defaultVariantShouldContainNoUri() throws Exception {
        BaseUri baseUriWrapper = new BaseUri();

        assertThat(baseUriWrapper.get()).isNull();
    }
}