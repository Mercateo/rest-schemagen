package com.mercateo.common.rest.schemagen.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mercateo.common.rest.schemagen.types.MessageData;

public class ValidationErrors {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final List<ValidationError> validationErrors;

        private Builder() {
            validationErrors = new ArrayList<>();
        }

        public Builder addError(ValidationErrorCodeContainer code, String path) {
            HashMap<MessageKey, String> entries = new HashMap<>();
            entries.put(MessageKey.path, path);
            addError(new ValidationError(code, entries));
            return this;
        }

        public Builder addError(ValidationError validationError) {
            validationErrors.add(validationError);
            return this;
        }

        public Data build() {
            return new Data(validationErrors.toArray(new ValidationError[validationErrors.size()]));
        }

    }

    public static class Data implements MessageData {
        public final ValidationError[] validationErrors;

        public Data(ValidationError[] validationErrors) {
            this.validationErrors = Arrays.copyOf(validationErrors, validationErrors.length);
        }
    }

    public static class ValidationError {

        @JsonIgnore
        public final HashMap<MessageKey, String> entries;

        @JsonAnySetter
        public void add(MessageKey key, String value) {
            entries.put(key, value);
        }

        @JsonAnyGetter
        public HashMap<MessageKey, String> getMap() {
            return entries;
        }

        public ValidationError(ValidationErrorCodeContainer code, Map<MessageKey, String> otherEntries) {
            HashMap<MessageKey, String> entries = new HashMap<>();
            entries.put(MessageKey.validationErrorCode, code.name());
            if (otherEntries != null) {
                entries.putAll(otherEntries);
            }
            this.entries = entries;
        }
    }

    public enum MessageKey {
        validationErrorCode, path, minimum, maximum
    }

    public enum ValidationErrorCode implements ValidationErrorCodeContainer {
        REQUIRED, UNKNOWN, STRING_LENGTH_SHORT, STRING_LENGTH_LONG, DUPLICATE,
        @Deprecated NO_PACKSTATION_ALLOWED,
        @Deprecated NO_POST_OFFICE_BOX_ALLOWED, @Deprecated NO_VALID_EMAIL, UNRECOGNIZED_FIELD,
        VALUE_BELOW_MIN, VALUE_ABOVE_MAX, @Deprecated NO_VALID_ZIP, @Deprecated USER_EXISTS, WRONG_PASSWORD
    }

    public interface ValidationErrorCodeContainer {
        String name();
    }
}