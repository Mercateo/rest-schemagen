package com.mercateo.common.rest.schemagen.link.helper;

import java.net.URI;
import java.util.List;
import java.util.Map;

public interface BaseUriCreator {
    /**
     * create base uri from request origin and raw request headers
     * @param requestBaseUri default base Uri
     * @param requestHeaders request headers as string multimap
     * @return base uri for link targets
     */
    URI createBaseUri(URI requestBaseUri, Map<String, List<String>> requestHeaders);

    /**
     * create base uri from request origin and wrapped request headers
     * @param requestBaseUri default base Uri
     * @param requestHeaders wrapped request headers
     * @return base uri for link targets
     */
    URI createBaseUri(URI requestBaseUri, HttpRequestHeaders requestHeaders);
}
