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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import jakarta.validation.constraints.Size;

public class SizeConstraintsTest {

    @Test
    public void constructorThrowsWithInvalidValues() {
        Size size = mock(Size.class);
        when(size.max()).thenReturn(2);
        when(size.min()).thenReturn(5);

        assertThatThrownBy(
                () -> new SizeConstraints(size)
        ).isInstanceOf(IllegalArgumentException.class).hasMessage("Minimum value 5 is larger than maximum value 2");
    }

    @Test
    public void constructorThrowsWithNegativeMinValue() {
        Size size = mock(Size.class);
        when(size.max()).thenReturn(2);
        when(size.min()).thenReturn(-2);

        assertThatThrownBy(
                () -> new SizeConstraints(size)
        ).isInstanceOf(IllegalArgumentException.class).hasMessage("Supplied arguments must be non-negative");
    }

    @Test
    public void defaultOperation() {
        final Optional<Integer> max = Optional.of(7);
        final Optional<Integer> min = Optional.of(4);

        Size size = mock(Size.class);
        when(size.max()).thenReturn(max.get());
        when(size.min()).thenReturn(min.get());

        final SizeConstraints sizeConstraints = new SizeConstraints(size);

        assertThat(sizeConstraints.getMax()).isEqualTo(max);
        assertThat(sizeConstraints.getMin()).isEqualTo(min);
    }

    @Test
    public void emptyConstraint() {
        final SizeConstraints sizeConstraints = SizeConstraints.empty();

        assertThat(sizeConstraints.getMax()).isEmpty();
        assertThat(sizeConstraints.getMin()).isEmpty();
    }

}
