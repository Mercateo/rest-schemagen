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
package com.mercateo.common.rest.schemagen.generator;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
