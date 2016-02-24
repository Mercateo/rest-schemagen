package com.mercateo.common.rest.schemagen;

import java.util.Optional;

public class ValueConstraints {

    private static final ValueConstraints EMPTY_CONSTRAINTS = new ValueConstraints();

    private Optional<Long> max;

    private Optional<Long> min;

    public Optional<Long> getMax() {
        return max;
    }

    public Optional<Long> getMin() {
        return min;
    }

    public ValueConstraints(Optional<Long> max, Optional<Long> min) {
        if (max.flatMap(x -> min.map(y -> y > x)).orElse(false)) {
            throw new IllegalArgumentException(String.format(
                    "Minimum value %s is larger than maximum value %s", min.get(), max.get()));
        }
        this.max = max;
        this.min = min;
    }

    public static ValueConstraints empty() {
        return EMPTY_CONSTRAINTS;
    }

    private ValueConstraints() {
        this.max = Optional.empty();
        this.min = Optional.empty();
    }
}
