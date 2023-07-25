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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class ValueConstraintsTest {

    @Test
    public void constructorThrowsWithInvalidValues() {
        assertThatThrownBy(
                () -> new ValueConstraints(Optional.of(2l), Optional.of(5l))
        ).isInstanceOf(IllegalArgumentException.class).hasMessage("Minimum value 5 is larger than maximum value 2");
    }

    @Test
    public void defaultOperation() {
        final Optional<Long> max = Optional.of(7l);
        final Optional<Long> min = Optional.of(4l);
        final ValueConstraints valueConstraints = new ValueConstraints(max, min);

        assertThat(valueConstraints.getMax()).isEqualTo(max);
        assertThat(valueConstraints.getMin()).isEqualTo(min);
    }

    @Test
    public void emptyConstraint() {
        final ValueConstraints valueConstraints = ValueConstraints.empty();

        assertThat(valueConstraints.getMax()).isEmpty();
        assertThat(valueConstraints.getMin()).isEmpty();
    }

}
