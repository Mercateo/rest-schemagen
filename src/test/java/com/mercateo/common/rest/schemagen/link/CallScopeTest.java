package com.mercateo.common.rest.schemagen.link;

import com.mercateo.common.rest.schemagen.parameter.CallContext;
import com.mercateo.reflection.Call;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CallScopeTest {

    static class Dummy {
        @SuppressWarnings("SameParameterValue")
        String getName(String base, Integer id) {
            return "";
        }
    }

    @Mock
    private CallContext callContext;

    @Test
    public void shouldTransportCallContext() throws Exception {
        final Call<Dummy> call = Call.of(Dummy.class, d -> d.getName(null, null));
        final CallScope callScope = new CallScope(call.declaringClass(), call.method(), call.args(), callContext);

        assertThat(callScope.getCallContext()).contains(callContext);
    }

    @Test
    public void toStringRepresentationShould() throws Exception {
        final Call<Dummy> call = Call.of(Dummy.class, d -> d.getName(null, null));
        final CallScope callScope = new CallScope(call.declaringClass(), call.method(), call.args(), callContext);

        assertThat(callScope.toString()).contains(call.method().getName()).contains("callContext=");
    }




}
