package com.mercateo.common.rest.schemagen;

import org.junit.Test;

import javax.validation.constraints.Size;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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