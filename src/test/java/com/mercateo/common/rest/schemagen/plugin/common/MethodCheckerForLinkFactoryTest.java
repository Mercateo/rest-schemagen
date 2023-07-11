package com.mercateo.common.rest.schemagen.plugin.common;

import com.mercateo.common.rest.schemagen.plugin.MethodCheckerForLink;
import jakarta.ws.rs.core.SecurityContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
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
