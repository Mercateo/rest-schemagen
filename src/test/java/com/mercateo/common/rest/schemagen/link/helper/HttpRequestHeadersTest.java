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
package com.mercateo.common.rest.schemagen.link.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;

public class HttpRequestHeadersTest {

    @Test
    public void shouldReturnAllHeaders() throws Exception {
        final HashMap<String, List<String>> requestHeaders = new HashMap<>();
        requestHeaders.put("foo", Collections.singletonList("bar"));
        requestHeaders.put("baz", Collections.singletonList("qux"));

        final HttpRequestHeaders headers = new HttpRequestHeaders(requestHeaders);

        assertThat(headers.getHeaders()).containsOnly(entry("foo", Collections.singletonList("bar")), entry("baz",
                Collections.singletonList("qux")));
    }

    @Test
    public void shouldIgnoreCaseOfName() throws Exception {
        final HashMap<String, List<String>> requestHeaders = new HashMap<>();
        requestHeaders.put("foo", Collections.singletonList("bar"));

        final HttpRequestHeaders headers = new HttpRequestHeaders(requestHeaders);

        assertThat(headers.getValues("Foo")).containsExactly("bar");
    }

    @Test
    public void shouldFilterNullValues() throws Exception {
        final HashMap<String, List<String>> requestHeaders = new HashMap<>();
        requestHeaders.put("foo", Arrays.asList("bar", null));

        final HttpRequestHeaders headers = new HttpRequestHeaders(requestHeaders);

        assertThat(headers.getValues("foo")).containsExactly("bar");
    }

    @Test
    public void shouldCreateRequestHeaders() throws Exception {
        final HashMap<String, List<String>> requestHeaders = new HashMap<>();
        requestHeaders.put("foo", Arrays.asList("baz", "qux"));
        requestHeaders.put("bar", Collections.singletonList("quux"));

        final HttpRequestHeaders headers = new HttpRequestHeaders(requestHeaders);

        assertThat(headers.getValues("foo")).containsExactly("baz", "qux");
        assertThat(headers.getValues("bar")).containsExactly("quux");
    }

    @Test
    public void shouldIgnoreUppercase() throws Exception {
        final HashMap<String, List<String>> requestHeaders = new HashMap<>();
        requestHeaders.put("foo", Arrays.asList("baz", "qux"));
        requestHeaders.put("Foo", Collections.singletonList("quux"));

        final HttpRequestHeaders headers = new HttpRequestHeaders(requestHeaders);

        assertThat(headers.getValues("foo")).containsExactlyInAnyOrder("baz", "qux", "quux");
    }

    @Test
    public void shouldCreateCopy() throws Exception {
        final HashMap<String, List<String>> requestHeaders = new HashMap<>();
        requestHeaders.put("foo", Collections.singletonList("bar"));

        final HttpRequestHeaders headers = new HttpRequestHeaders(requestHeaders);

        final HttpRequestHeaders headersCopy = new HttpRequestHeaders(headers);

        assertThat(headersCopy.getValues("foo")).containsExactly("bar");
    }
}
