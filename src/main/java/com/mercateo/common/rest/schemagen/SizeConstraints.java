/*
 * Copyright © 2015 Mercateo AG (http://www.mercateo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mercateo.common.rest.schemagen;

import java.util.Optional;

import jakarta.validation.constraints.Size;

public class SizeConstraints {

    private static final SizeConstraints EMPTY_CONSTRAINTS = new SizeConstraints();

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
        if (min < 0) {
            throw new IllegalArgumentException("Supplied arguments must be non-negative");
        }
        this.max = max == Integer.MAX_VALUE ? Optional.empty() : Optional.of(max);
        this.min = min == 0 ? Optional.empty() : Optional.of(min);
    }

    public static SizeConstraints empty() {
        return EMPTY_CONSTRAINTS;
    }

    private SizeConstraints() {
        this.max = Optional.empty();
        this.min = Optional.empty();
    }
}
