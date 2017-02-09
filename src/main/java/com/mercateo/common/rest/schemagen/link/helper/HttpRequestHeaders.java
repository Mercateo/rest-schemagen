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
        this.requestHeaders = other.requestHeaders;
    }

    public List<String> getValues(String headername) {
        return requestHeaders.getOrDefault(headername.toLowerCase(), Collections.emptyList());
    }
}
