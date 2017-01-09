package com.mercateo.common.rest.schemagen.link.helper;

import java.net.URI;
import java.util.List;
import java.util.Map;

public interface BaseUriCreator {
    URI createBaseUri(URI requestBaseUri, Map<String, List<String>> requestHeaders);
}
