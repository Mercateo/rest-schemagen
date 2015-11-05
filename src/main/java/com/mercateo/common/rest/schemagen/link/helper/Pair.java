package com.mercateo.common.rest.schemagen.link.helper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable iff both LeftType and RightType are immutable.
 *
 * A simple immutable pair class (therefore final fields and no setters),
 * especially useful for returning two values.
 *
 * @author till
 *
 * @param <LeftType>
 * @param <RightType>
 */
public class Pair<LeftType, RightType> {

    public final LeftType left;

    public final RightType right;

    protected Pair() {
        left = null;
        right = null;
    }

    public Pair(LeftType left, RightType right) {
        this.left = left;
        this.right = right;
    }

    /**
     * utility method for constructing a pair without re-typing the parameters
     * so you can write <br>
     * <code>Pair&lt;String,BigDecimal&gt; pair= Pair.make("Test", BigDecimal.ZERO);</code>
     * <br>
     * instead of<br>
     * <code>Pair&lt;String,BigDecimal&gt; pair= new Pair&lt;String,BigDecimal&gt;("Test", BigDecimal.ZERO);</code>
     *
     * @param left
     * @param right
     * @param <S>
     *            type of left (implicitly computed)
     * @param <T>
     *            type of right (implicitly computed)
     * @return new Pair&lt;S,T&gt(left, right)
     */
    public static <S, T> Pair<S, T> make(S left, T right) {
        return new Pair<S, T>(left, right);
    }

    @Override
    public String toString() {
        return "Pair<" + left + "," + right + ">";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((left == null) ? 0 : left.hashCode());
        result = prime * result + ((right == null) ? 0 : right.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        @SuppressWarnings("unchecked")
        Pair<Object, Object> other = (Pair<Object, Object>) obj;
        if (left == null) {
            if (other.left != null) {
                return false;
            }
        } else if (!left.equals(other.left)) {
            return false;
        }
        if (right == null) {
            if (other.right != null) {
                return false;
            }
        } else if (!right.equals(other.right)) {
            return false;
        }
        return true;
    }

    public static <S, T> Comparator<Pair<S, T>> createComparatorByLeft(
            final Comparator<? super S> comparator) {
        return new Comparator<Pair<S, T>>() {
            @Override
            public int compare(Pair<S, T> o1, Pair<S, T> o2) {
                return comparator.compare(o1.left, o2.left);
            }
        };
    }

    public static <S, T> Comparator<Pair<S, T>> createComparatorByRight(
            final Comparator<? super T> comparator) {
        return new Comparator<Pair<S, T>>() {
            @Override
            public int compare(Pair<S, T> o1, Pair<S, T> o2) {
                return comparator.compare(o1.right, o2.right);
            }
        };
    }

    public static <S, T> Map<S, List<Pair<S, T>>> groupByLeft(Pair<S, T>[] pairs) {
        final Map<S, List<Pair<S, T>>> map = new HashMap<S, List<Pair<S, T>>>();
        if (pairs != null) {
            for (Pair<S, T> pair : pairs) {
                List<Pair<S, T>> list = map.get(pair.left);
                if (list == null) {
                    list = new ArrayList<Pair<S, T>>();
                    map.put(pair.left, list);
                }
                list.add(pair);
            }
        }
        return map;
    }
}
