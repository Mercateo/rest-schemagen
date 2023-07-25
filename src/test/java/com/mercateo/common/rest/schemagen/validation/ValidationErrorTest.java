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
package com.mercateo.common.rest.schemagen.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

public class ValidationErrorTest {

    @Test
    public void testValidationErrorsWithoutEntries() {
        final ValidationErrors.Data validationErrorsData = ValidationErrors.builder().build();

        assertThat(validationErrorsData.validationErrors).isEmpty();
    }

    @Test
    public void testAddValidationErrorWithCodeAndPath() {
        final String errorLocation = "#/foo";
        final TestValidationErrorCode errorCode = TestValidationErrorCode.REQUIRED;
        final ValidationErrors.Data validationErrorsData = ValidationErrors.builder().addError(
                errorCode, errorLocation).build();

        assertThat(validationErrorsData.validationErrors).hasSize(1);
        assertThat(validationErrorsData.validationErrors[0].getMap()) //
                .hasSize(2) //
                .containsEntry(ValidationErrors.MessageKey.path, errorLocation) //
                .containsEntry(ValidationErrors.MessageKey.validationErrorCode,
                        errorCode.toString());
    }

    @Test
    public void testValidationErrorAdd() {
        final TestValidationErrorCode errorCode = TestValidationErrorCode.DUPLICATE;

        final ValidationErrors.ValidationError validationError = new ValidationErrors.ValidationError(errorCode, new HashMap<>());

        assertThat(validationError.getMap()) //
                .hasSize(1) //
                .containsEntry(ValidationErrors.MessageKey.validationErrorCode, errorCode.toString());

        final String maximumValue = "5";
        validationError.add(ValidationErrors.MessageKey.maximum, maximumValue);

        assertThat(validationError.getMap()) //
                .hasSize(2) //
                .containsEntry(ValidationErrors.MessageKey.validationErrorCode, errorCode.toString())
                .containsEntry(ValidationErrors.MessageKey.maximum, maximumValue);
    }

    public enum TestValidationErrorCode implements ValidationErrors.ValidationErrorCodeContainer {
        REQUIRED, DUPLICATE
    }
}