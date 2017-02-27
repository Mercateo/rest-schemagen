package com.mercateo.common.rest.schemagen.json.mapper;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mercateo.common.rest.schemagen.JsonProperty;

interface JsonPropertyMapper {
    ObjectNode toJson(JsonProperty jsonProperty);
}
