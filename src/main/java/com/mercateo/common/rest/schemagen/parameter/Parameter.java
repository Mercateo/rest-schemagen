/**
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
package com.mercateo.common.rest.schemagen.parameter;

import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class Parameter<T> {

    private final CallContext context;

    private final List<T> allowedValues;

    private final T defaultValue;

    private Parameter(CallContext context, List<T> allowedValues, T defaultValue) {
        this.context = context;
        this.allowedValues = allowedValues;
        this.defaultValue = defaultValue;

        checkState(get() != null,
                "parameter should have at least one allowed or default value set");
        checkState(!context.hasParameter(get()), "parameter values can only be used once");

        context.addParameter(get(), this);
    }

    public static CallContext createContext() {
        return new CallContext();
    }

    public static <T> Builder<T> builderFor(Class<T> parameterClass) {
        return createContext().builderFor(parameterClass);
    }

    public CallContext context() {
        return context;
    }

    public T get() {
        return allowedValues.isEmpty() ? defaultValue : allowedValues.get(0);
    }

    public boolean hasAllowedValues() {
        return !allowedValues.isEmpty();
    }

    public List<T> getAllowedValues() {
        return allowedValues;
    }

    public boolean hasDefaultValue() {
        return defaultValue != null;
    }

    public Object getDefaultValue() {
        checkState(defaultValue != null, "default value null can not be used");
        return defaultValue;
    }

    public static class Builder<T> {

        private final Class<T> parameterClass;

        private final CallContext context;

        private final List<T> allowedValues;

        private boolean nothingAllowed;

        private T defaultValue;

        Builder(Class<T> parameterClass, CallContext context) {
            this.parameterClass = parameterClass;
            this.context = context;
            this.allowedValues = new ArrayList<>();
            this.nothingAllowed = false;
        }

        @SafeVarargs
        public final Builder<T> allowValues(T... allowedValues) {
            if (allowedValues.length == 0) {
                nothingAllowed = true;
            }
            for (T allowedValue : allowedValues) {
                if (allowedValue != null) {
                    this.allowedValues.add(allowedValue);
                }
            }
            return this;
        }

        public <U> Builder<T> allowValues(Collection<U> values, Function<U, T> mapper) {
            allowValues(values.stream().map(mapper).toArray(this::createArray));
            return this;
        }

        @SuppressWarnings("unchecked")
        private T[] createArray(int size) {
            // noinspection unchecked
            return (T[]) Array.newInstance(parameterClass, size);
        }

        public Builder<T> defaultValue(T defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Parameter<T> build() {
            return new Parameter<>(context, nothingAllowed ? Collections.emptyList()
                    : allowedValues, defaultValue);
        }

        public boolean isEmpty() {
            return nothingAllowed || (allowedValues.isEmpty() && defaultValue == null);
        }
    }
}
