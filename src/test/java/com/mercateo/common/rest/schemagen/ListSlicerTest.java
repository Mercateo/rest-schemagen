package com.mercateo.common.rest.schemagen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.List;

import jersey.repackaged.com.google.common.collect.Lists;

import org.junit.Test;

@SuppressWarnings("boxing")
public class ListSlicerTest {

    public static final int DEFAULT_LIMIT = 100;
    public static final int DEFAULT_OFFSET = 0;

    private final List<Integer> list = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    @Test
    public void testSubList() {
        List<Integer> actual = ListSlicer.withInterval(1, DEFAULT_LIMIT).create(3, 2).createSliceOf(list).members;
        assertEquals(Lists.newArrayList(4, 5), actual);
    }

    @Test
    public void testSubList_CheckMinLimit() {
        List<Integer> actual = ListSlicer.withInterval(3, 100).create(3, 2).createSliceOf(list).members;
        assertEquals(Lists.newArrayList(4, 5, 6), actual);
    }

    @Test
    public void testSubList_CheckMaxLimit() {
        List<Integer> actual = ListSlicer.withInterval(1, 3).create(3, 5).createSliceOf(list).members;
        assertEquals(Lists.newArrayList(4, 5, 6), actual);
    }

    @Test
    public void testSubList_CheckDefaultLimit() {
        List<Integer> actual = ListSlicer.withInterval(1, 3).create(3, null).createSliceOf(list).members;
        assertEquals(Lists.newArrayList(4, 5, 6), actual);
    }

    @Test
    public void testSubList_CheckDefaultOffset() {
        List<Integer> actual = ListSlicer.withDefaultInterval().create(null, 5).createSliceOf(list).members;
        assertEquals(Lists.newArrayList(1, 2, 3, 4, 5), actual);
    }

    @Test
    public void testSlicerDefaultLimit() {
        final ListSlicer.SliceDefaults slicerDefaults = ListSlicer.createDefaults(DEFAULT_LIMIT, DEFAULT_OFFSET);

        assertThat(slicerDefaults.determineLimit(null)).isEqualTo(DEFAULT_LIMIT);

        final int limit = 10;
        assertThat(slicerDefaults.determineLimit(limit)).isEqualTo(limit);
    }

    @Test
    public void testSlicerDefaultOffset() {
        final ListSlicer.SliceDefaults slicerDefaults = ListSlicer.createDefaults(DEFAULT_LIMIT, DEFAULT_OFFSET);

        assertThat(slicerDefaults.determineOffset(null)).isEqualTo(DEFAULT_OFFSET);

        final int offset = 50;
        assertThat(slicerDefaults.determineOffset(offset)).isEqualTo(offset);
    }

}
