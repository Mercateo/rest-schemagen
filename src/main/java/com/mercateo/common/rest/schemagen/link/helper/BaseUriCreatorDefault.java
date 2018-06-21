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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseUriCreatorDefault implements BaseUriCreator {

    private static final Logger log = LoggerFactory.getLogger(BaseUriCreatorDefault.class);

    static final String TLS_STATUS_HEADER = "Front-End-Https";

    static final String FORWARDED_HOST_HEADER = "X-Forwarded-Host";

    static final String FORWARDED_PROTO_HEADER = "x-forwarded-proto";

    static final String SERVICE_BASE_HEADER = "Service-Base-Path";

    private static final String HTTPS_SCHEME = "https";

    @Override
    public URI createBaseUri(final URI requestBaseUri, final Map<String, List<String>> requestHeaders) {
        return createBaseUri(requestBaseUri, new HttpRequestHeaders(requestHeaders));
    }

    @Override
    public URI createBaseUri(final URI requestBaseUri, HttpRequestHeaders requestHeaders) {
        try {
            String scheme = requestBaseUri.getScheme();

            final Optional<String> tlsStatusHeader = headerValue(requestHeaders, TLS_STATUS_HEADER);
            final Optional<String> forwardedProtoHeader = firstHeaderValue(requestHeaders, FORWARDED_PROTO_HEADER);
            if (tlsStatusHeader.map(String::toLowerCase).filter("on"::equals).isPresent() ||
                    forwardedProtoHeader.map(String::toLowerCase).filter("https"::equals).isPresent()) {
                scheme = HTTPS_SCHEME;
            }

            Optional<String> forwardedHostHeader = firstHeaderValue(requestHeaders, FORWARDED_HOST_HEADER);
            String host = forwardedHostHeader.orElse(requestBaseUri.getHost());
            String path = headerValue(requestHeaders, SERVICE_BASE_HEADER).orElse(requestBaseUri.getPath());
            boolean useDefaultPort = tlsStatusHeader.isPresent() || forwardedProtoHeader.isPresent()
                    || forwardedHostHeader.isPresent();
            int port = useDefaultPort ? -1 : requestBaseUri.getPort();

            return new URI(scheme, requestBaseUri.getUserInfo(), host, port, path, requestBaseUri.getQuery(),
                    requestBaseUri.getFragment());
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    private Optional<String> firstHeaderValue(HttpRequestHeaders requestHeaders, String parameterName) {
        return headerValue(requestHeaders, parameterName).map(BaseUriCreatorDefault::firstEntry);
    }

    private static Optional<String> headerValue(final HttpRequestHeaders requestHeaders, final String parameterName) {
        final Optional<String> headerValue = requestHeaders.getValues(parameterName).stream().findFirst();
        headerValue.ifPresent(value -> log.debug("use value '{}' from header {}", value, parameterName));
        return headerValue;
    }

    private static String firstEntry(String headerValue) {
        return headerValue.split(", ")[0];
    }
}
