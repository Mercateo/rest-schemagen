package com.mercateo.common.rest.schemagen.link.injection;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import org.glassfish.hk2.api.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseUriFactory implements Factory<BaseUri> {
    public static final String TLS_STATUS_HEADER = "Front-End-Https";

    public static final String HOST_HEADER = "X-Forwarded-Host";

    public static final String SERVICE_BASE_HEADER = "Service-Base-Path";

    public static final String HTTP_SCHEME = "http";

    public static final String HTTPS_SCHEME = "https";

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseUriFactory.class);

    private UriInfo uriInfo;

    private HttpHeaders httpHeaders;

    @Inject
    public BaseUriFactory(UriInfo uriInfo, HttpHeaders httpHeaders) {
        super();
        this.uriInfo = uriInfo;
        this.httpHeaders = httpHeaders;
    }

    @Override
    public BaseUri provide() {
        return getBaseUri(uriInfo, httpHeaders);
    }

    public static BaseUri getBaseUri(UriInfo uriInfo, HttpHeaders httpHeaders) {
        URI baseUri = uriInfo.getBaseUri();

        try {
            String scheme = baseUri.getScheme();
            Optional<String> tlsStatusHeader = headerValue(httpHeaders, TLS_STATUS_HEADER);
            if (tlsStatusHeader.filter("On"::equals).isPresent()) {
                scheme = HTTPS_SCHEME;
            }
            Optional<String> hostHeader = headerValue(httpHeaders, HOST_HEADER).map(host -> host
                    .split(", ")[0]);
            String host = hostHeader.orElse(baseUri.getHost());
            String path = headerValue(httpHeaders, SERVICE_BASE_HEADER).orElse(baseUri.getPath());
            boolean useDefaultPort = tlsStatusHeader.isPresent() || hostHeader.isPresent();
            int port = useDefaultPort ? -1 : baseUri.getPort();

            return new BaseUri(new URI(scheme, baseUri.getUserInfo(), host, port, path, baseUri
                    .getQuery(), baseUri.getFragment()));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void dispose(BaseUri uri) {
        // no body
    }

    private static Optional<String> headerValue(final HttpHeaders httpHeaders,
            final String parameterName) {
        final List<String> requestHeader = getRequestHeader(httpHeaders, parameterName);
        if (!requestHeader.isEmpty()) {
            String valueFromHeader = requestHeader.get(0);
            LOGGER.debug("use value '{}' from header {}", valueFromHeader, parameterName);
            return Optional.ofNullable(valueFromHeader);
        }
        return Optional.empty();
    }

    private static List<String> getRequestHeader(final HttpHeaders httpHeaders,
            final String headerName) {
        if (httpHeaders != null) {
            List<String> requestHeader = httpHeaders.getRequestHeader(headerName);
            if (requestHeader != null) {
                return requestHeader;
            }
        }
        return Collections.emptyList();
    }
}
