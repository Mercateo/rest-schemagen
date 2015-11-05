package com.mercateo.common.rest.schemagen.generator;

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

    public void setNotEmptyStringWithSize(String notEmptyStringWithSize) {
        this.notEmptyStringWithSize = notEmptyStringWithSize;
    }
}
