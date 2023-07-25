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
package com.mercateo.common.rest.schemagen.parameter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CallContextTest {

    private CallContext context;

    @BeforeEach
    public void setUp() {
        context = new CallContext();
    }

    @Test
    public void testViewClasses() {
        assertThat(context.getAdditionalObjectsFor(Class.class).isPresent()).isFalse();

        context.addAdditionalObjects(Class.class, getClass());
        context.addAdditionalObjects(Class.class, getClass());

        assertThat(context.getAdditionalObjectsFor(Class.class).get()).isNotEmpty();
    }
}