package com.mercateo.common.rest.schemagen.util;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class OptionalUtil {

    private OptionalUtil() {
    }
    
    @SafeVarargs
    public static <T> List<T> collect(Optional<T>... optionals) {
        return Stream.of(optionals)
                .flatMap(o -> o.map(Stream::of).orElse(Stream.empty()))
                .collect(Collectors.toList());
    }
}
