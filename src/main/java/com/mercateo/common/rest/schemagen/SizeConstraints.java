package com.mercateo.common.rest.schemagen;

import java.util.Optional;

import javax.validation.constraints.Size;

public class SizeConstraints {

    private Optional<Integer> max;

    private Optional<Integer> min;

    public Optional<Integer> getMax() {
        return max;
    }

    public Optional<Integer> getMin() {
        return min;
    }

    public SizeConstraints(Size size) {
        this(size.max(), size.min());
    }

    @SuppressWarnings("boxing")
    private SizeConstraints(int max, int min) {
        if (min > max) {
            throw new IllegalArgumentException(String.format(
                    "Minimum value %s is larger than maximum value %s", min, max));
        }
        if (min < 0 || max < 0) {
            throw new IllegalArgumentException("Supplied arguments must be non-negative");
        }
        this.max = max == Integer.MAX_VALUE ? Optional.empty() : Optional.of(max);
        this.min = min == 0 ? Optional.empty() : Optional.of(min);
    }

    public static SizeConstraints empty() {
        return new SizeConstraints();
    }

    private SizeConstraints() {
        this.max = Optional.empty();
        this.min = Optional.empty();
    }
}
