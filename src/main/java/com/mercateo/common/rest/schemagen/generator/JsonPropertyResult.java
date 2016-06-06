package com.mercateo.common.rest.schemagen.generator;

import com.mercateo.common.rest.schemagen.JsonProperty;
import com.mercateo.common.rest.schemagen.internal.DataClassStyle;
import com.mercateo.common.rest.schemagen.internal.TupleStyle;
import org.immutables.value.Value;

import java.util.Set;

@Value.Immutable
@TupleStyle
public interface JsonPropertyResult {
    JsonProperty getRoot();
    Set<JsonProperty> getReferencedElements();
}
