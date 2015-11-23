package com.mercateo.common.rest.schemagen;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import jersey.repackaged.com.google.common.collect.Lists;

@SuppressWarnings("boxing")
public class ListSlicerTest {

    public static final int DEFAULT_LIMIT = 100;
    public static final int DEFAULT_OFFSET = 0;

    private final List<Integer> list = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    @Test
    public void testComputationOfDefaults() {
        final ListSlicer listSlicer = ListSlicer.withInterval(5, 10).create(1, 15);
        assertThat(listSlicer.getLimit()).isEqualTo(10);
        assertThat(listSlicer.getOffset()).isEqualTo(1);
        assertThat(listSlicer.toString()).isEqualTo("ListSlicer [offset=1, limit=10]");
    }

    @Test
    public void testSubList() {
        List<Integer> actual = ListSlicer.withInterval(1, DEFAULT_LIMIT).create(3, 2).createSliceOf(list).members;
        assertThat(actual).isEqualTo(Lists.newArrayList(4,5));
    }

    @Test
    public void testSubList_CheckMinLimit() {
        List<Integer> actual = ListSlicer.withInterval(3, 100).create(3, 2).createSliceOf(list).members;
        assertThat(actual).isEqualTo(Lists.newArrayList(4, 5, 6));
    }

    @Test
    public void testSubList_CheckMaxLimit() {
        List<Integer> actual = ListSlicer.withInterval(1, 3).create(3, 5).createSliceOf(list).members;
        assertThat(actual).isEqualTo(Lists.newArrayList(4, 5, 6));
    }

    @Test
    public void testSubList_CheckDefaultLimit() {
        List<Integer> actual = ListSlicer.withInterval(1, 3).create(3, null).createSliceOf(list).members;
        assertThat(actual).isEqualTo(Lists.newArrayList(4, 5, 6));
    }

    @Test
    public void testSubList_CheckDefaultOffset() {
        List<Integer> actual = ListSlicer.withDefaultInterval().create(null, 5).createSliceOf(list).members;
        assertThat(actual).isEqualTo(Lists.newArrayList(1, 2, 3, 4, 5));
    }

}
