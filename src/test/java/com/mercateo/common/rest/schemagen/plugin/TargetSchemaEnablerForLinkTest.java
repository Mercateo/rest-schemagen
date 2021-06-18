package com.mercateo.common.rest.schemagen.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.mercateo.common.rest.schemagen.link.Scope;

import org.junit.Test;

public class TargetSchemaEnablerForLinkTest {
    @Test
    public void shouldBeCreateableFromPredicate() {
        final Scope scope = mock(Scope.class);
        final TargetSchemaEnablerForLink methodCheckerForLink = TargetSchemaEnablerForLink
                .fromPredicate(x -> x == scope);

        assertThat(methodCheckerForLink.test(scope)).isTrue();
        assertThat(methodCheckerForLink.test(null)).isFalse();
    }

}