package com.mercateo.common.rest.schemagen.link.helper;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseUriCreator {

    private static final Logger log = LoggerFactory.getLogger(BaseUriCreator.class);

    static final String TLS_STATUS_HEADER = "Front-End-Https";

    static final String HOST_HEADER = "X-Forwarded-Host";

    static final String SERVICE_BASE_HEADER = "Service-Base-Path";

    private static final String HTTPS_SCHEME = "https";

    public URI createBaseUri(final URI requestBaseUri, final Map<String, List<String>> requestHeaders) {

        try {
            String scheme = requestBaseUri.getScheme();
            Optional<String> tlsStatusHeader = headerValue(requestHeaders, TLS_STATUS_HEADER);
            if (tlsStatusHeader.filter("On"::equals).isPresent()) {
                scheme = HTTPS_SCHEME;
            }
            Optional<String> hostHeader = headerValue(requestHeaders, HOST_HEADER).map(host -> host.split(", ")[0]);
            String host = hostHeader.orElse(requestBaseUri.getHost());
            String path = headerValue(requestHeaders, SERVICE_BASE_HEADER).orElse(requestBaseUri.getPath());
            boolean useDefaultPort = tlsStatusHeader.isPresent() || hostHeader.isPresent();
            int port = useDefaultPort ? -1 : requestBaseUri.getPort();

            return new URI(scheme, requestBaseUri.getUserInfo(), host, port, path, requestBaseUri.getQuery(),
                    requestBaseUri.getFragment());
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Optional<String> headerValue(final Map<String, List<String>> requestHeaders,
            final String parameterName) {
        final List<String> requestHeader = getRequestHeader(requestHeaders, parameterName);
        if (!requestHeader.isEmpty()) {
            String valueFromHeader = requestHeader.get(0);
            log.debug("use value '{}' from header {}", valueFromHeader, parameterName);
            return Optional.ofNullable(valueFromHeader);
        }
        return Optional.empty();
    }

    private static List<String> getRequestHeader(final Map<String, List<String>> requestHeaders,
            final String headerName) {
        if (requestHeaders != null && requestHeaders.containsKey(headerName)) {
            return requestHeaders.get(headerName);
        }
        return Collections.emptyList();
    }
}
