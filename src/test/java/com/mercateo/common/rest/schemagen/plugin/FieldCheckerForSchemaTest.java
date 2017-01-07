package com.mercateo.common.rest.schemagen.plugin;

import com.mercateo.common.rest.schemagen.parameter.CallContext;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class FieldCheckerForSchemaTest {

    @Test
    public void shouldBeCreateableFromBiPredicate() throws Exception {
        final CallContext enabledCallContext = mock(CallContext.class);
        final FieldCheckerForSchema fieldCheckerForSchema = FieldCheckerForSchema.fromBiPredicate((field, callContext) -> field == null && callContext == enabledCallContext);

        assertThat(fieldCheckerForSchema.test(null, enabledCallContext)).isTrue();
        assertThat(fieldCheckerForSchema.test(null, null)).isFalse();
    }
}