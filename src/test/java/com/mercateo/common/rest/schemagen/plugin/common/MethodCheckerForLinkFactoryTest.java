package com.mercateo.common.rest.schemagen.plugin.common;

import com.mercateo.common.rest.schemagen.plugin.MethodCheckerForLink;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.SecurityContext;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class MethodCheckerForLinkFactoryTest {

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private MethodCheckerForLinkFactory methodCheckerFactory;

    @Test
    public void testProvide() {
        final MethodCheckerForLink methodChecker = methodCheckerFactory.provide();
        assertThat(methodChecker).isNotNull();
    }
}