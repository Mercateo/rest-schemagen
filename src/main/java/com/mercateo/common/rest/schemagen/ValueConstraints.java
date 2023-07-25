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
