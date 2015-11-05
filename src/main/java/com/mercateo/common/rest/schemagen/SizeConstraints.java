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

    private SizeConstraints(int max, int min) {
        if (min > max)
            throw new IllegalArgumentException(String.format(
                    "Minimum value %s is larger than maximum value %s", Integer.valueOf(min),
                    Integer.valueOf(max)));
        if (min < 0 || max < 0)
            throw new IllegalArgumentException("Supplied arguments must be non-negative");
        if (max != Integer.MAX_VALUE)
            this.max = Optional.of(Integer.valueOf(max));
        else
            this.max = Optional.empty();
        if (min != 0)
            this.min = Optional.of(Integer.valueOf(min));
        else
            this.min = Optional.empty();
    }

    public static SizeConstraints empty() {
        return new SizeConstraints();
    }

    private SizeConstraints() {
        this.max = Optional.empty();
        this.min = Optional.empty();
    }
}
