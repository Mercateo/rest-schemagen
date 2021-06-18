package com.mercateo.common.rest.schemagen.plugin.common;

import static org.assertj.core.api.Assertions.assertThat;

import com.mercateo.common.rest.schemagen.link.Scope;
import com.mercateo.common.rest.schemagen.plugin.TargetSchemaEnablerForLink;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TargetSchemaEnablerForLinkFactoryTest {
    @Mock
    Scope scope;

    @InjectMocks
    private TargetSchemaEnablerForLinkFactory targetSchemaEnablerForLinkFactory;

    @Test
    public void testProvide() {
        final TargetSchemaEnablerForLink targetSchemaEnablerForLink = targetSchemaEnablerForLinkFactory.provide();
        assertThat(targetSchemaEnablerForLink.test(scope)).isTrue();
    }
}