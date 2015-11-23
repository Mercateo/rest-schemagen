package com.mercateo.common.rest.schemagen;

import java.util.Optional;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class ValueConstraints {
    private Optional<Long> max;

    private Optional<Long> min;

    public Optional<Long> getMax() {
        return max;
    }

    public Optional<Long> getMin() {
        return min;
    }

    public ValueConstraints(Max max, Min min)  {
        this(Optional.ofNullable(max).map(Max::value), Optional.ofNullable(min).map(Min::value));
    }

    private ValueConstraints(Optional<Long> max, Optional<Long> min) {
        if (max.flatMap(x -> min.map(y -> y > x)).orElse(false)) {
            throw new IllegalArgumentException(String.format(
                    "Minimum value %s is larger than maximum value %s", min, max));
        }
        this.max = max;
        this.min = min;
    }

    public static ValueConstraints empty() {
        return new ValueConstraints();
    }

    private ValueConstraints() {
        this.max = Optional.empty();
        this.min = Optional.empty();
    }

}
