package com.mercateo.common.rest.schemagen;

import java.util.ArrayList;

public class Something {
    public enum TEST {
        test1, test2
    }

    private String id;

    private ArrayList<String> list;

    private TEST test;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getList() {
        return list;
    }

    public void setList(ArrayList<String> list) {
        this.list = list;
    }

    public TEST getTest() {
        return test;
    }

    public void setTest(TEST test) {
        this.test = test;
    }
}