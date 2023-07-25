/*
 * Copyright © 2015 Mercateo AG (http://www.mercateo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mercateo.common.rest.schemagen.link;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mercateo.common.rest.schemagen.parameter.CallContext;
import com.mercateo.reflection.Call;

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
