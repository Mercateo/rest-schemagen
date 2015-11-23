package com.mercateo.common.rest.schemagen;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.mercateo.common.rest.schemagen.types.PaginatedList;

public class ListSlicer {

    private static final int DEFAULT_MIN_LIMIT = 5;

    private static final int DEFAULT_MAX_LIMIT = 2000;

    private static final int DEFAULT_OFFSET = 0;

    private final int offset;

    private final int limit;

    public <E> PaginatedList<E> createSliceOf(List<E> list) {
        List<E> slicedList = list.stream().skip(offset).limit(limit).collect(Collectors.toList());
        return new PaginatedList<>(list.size(), offset, limit, slicedList);
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }

    private ListSlicer(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public static ListSlicerBuilder withInterval(int minLimit, int maxLimit) {
        return new ListSlicerBuilder(minLimit, maxLimit, DEFAULT_OFFSET);
    }

    public static ListSlicerBuilder withDefaultInterval() {
        return withInterval(DEFAULT_MIN_LIMIT, DEFAULT_MAX_LIMIT);
    }

    public static class ListSlicerBuilder {

        private final int minLimit;

        private final int maxLimit;

        private final int defaultOffset;

        private ListSlicerBuilder(int minLimit, int maxLimit, int defaultOffset) {
            this.minLimit = minLimit;
            this.maxLimit = maxLimit;
            this.defaultOffset = defaultOffset;
        }

        public ListSlicer create(Integer offset, Integer limit) {
            return new ListSlicer(getConstrainedOffset(offset), getConstrainedLimit(limit));
        }

        @SuppressWarnings("boxing")
        private int getConstrainedOffset(Integer offset) {
            return Optional.ofNullable(offset).map(o -> Math.max(o, 0)).orElse(defaultOffset);
        }

        @SuppressWarnings("boxing")
        private int getConstrainedLimit(Integer limit) {
            return Optional.ofNullable(limit).map(l -> Math.min(Math.max(l, minLimit), maxLimit)).orElse(maxLimit);
        }
    }

    @Override
    public String toString() {
        return "ListSlicer [offset=" + offset + ", limit=" + limit + "]";
    }
}
