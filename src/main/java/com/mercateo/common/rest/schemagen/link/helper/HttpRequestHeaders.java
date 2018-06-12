/**
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HttpRequestHeaders {
    private final Map<String, List<String>> requestHeaders;

    public HttpRequestHeaders(Map<String, List<String>> requestHeaders) {

        HashMap<String, List<String>> normalizedRequestHeaders = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : requestHeaders.entrySet()) {
            String headerName = entry.getKey().toLowerCase();

            final List<String> values = normalizedRequestHeaders.computeIfAbsent(headerName, key -> new ArrayList<>());
            entry.getValue().stream().filter(Objects::nonNull).forEach(values::add);
            normalizedRequestHeaders.put(headerName, values);
        }

        this.requestHeaders = normalizedRequestHeaders;
    }

    public HttpRequestHeaders(HttpRequestHeaders other) {
        this.requestHeaders = other.getHeaders();
    }

    public List<String> getValues(String headername) {
        return requestHeaders.getOrDefault(headername.toLowerCase(), Collections.emptyList());
    }

    public Map<String, List<String>> getHeaders() {
        return requestHeaders;
    }
}
