package com.mercateo.common.rest.schemagen.plugin.common;

import com.mercateo.common.rest.schemagen.link.Scope;
import com.mercateo.common.rest.schemagen.plugin.TargetSchemaEnablerForLink;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
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
