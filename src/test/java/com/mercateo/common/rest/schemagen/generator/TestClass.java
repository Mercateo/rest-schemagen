package com.mercateo.common.rest.schemagen.generator;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class TestClass {
    @NotNull
    private String notNullString;

    @NotEmpty
    private String notEmptyString;

    @Size(min = 1, max = 10)
    private String sizeString;

    @Size(min = 10)
    @NotEmpty
    private String notEmptyStringWithSize;

    @Min(-4)
    @Max(2704)
    private int intWithValueConstraints;

    public String getNotNullString() {
        return notNullString;
    }

    public void setNotNullString(String notNullString) {
        this.notNullString = notNullString;
    }

    public String getNotEmptyString() {
        return notEmptyString;
    }

    public void setNotEmptyString(String notEmptyString) {
        this.notEmptyString = notEmptyString;
    }

    public String getSizeString() {
        return sizeString;
    }

    public void setSizeString(String sizeString) {
        this.sizeString = sizeString;
    }

    public String getNotEmptyStringWithSize() {
        return notEmptyStringWithSize;
    }

    public int getIntWithValueConstraints() {
        return intWithValueConstraints;
    }

    public void setIntWithValueConstraints(int intWithValueConstraints) {
        this.intWithValueConstraints = intWithValueConstraints;
    }

    public void setNotEmptyStringWithSize(String notEmptyStringWithSize) {
        this.notEmptyStringWithSize = notEmptyStringWithSize;
    }
}
