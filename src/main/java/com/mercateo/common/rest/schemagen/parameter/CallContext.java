package com.mercateo.common.rest.schemagen.parameter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.requireNonNull;

public class CallContext {

    private final Map<Object, Parameter<?>> parameters = new IdentityHashMap<>();

    private final Map<Class<?>, Set<?>> additionalObjects = new HashMap<>();

    public static CallContext create() {
        return new CallContext();
    }

    public <T> Parameter.Builder<T> builderFor(Class<T> parameterClass) {
        return new Parameter.Builder<>(parameterClass, this);
    }

    public boolean isEmpty() {
        return parameters.isEmpty();
    }

    public boolean hasParameter(Object value) {
        return parameters.containsKey(value);
    }

    public Parameter<?> getParameter(Object value) {
        return parameters.get(value);
    }

    <T> void addParameter(T value, Parameter<T> parameter) {
        parameters.put(value, parameter);
    }

    @SuppressWarnings("unchecked")
    public <T> CallContext addAdditionalObjects(Class<T> clazz, T... objects) {
        requireNonNull(clazz);

        final Set<T> set = Arrays.stream(objects != null ? objects : (T[]) new Object[0])
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (!set.isEmpty()) {
            if (additionalObjects.containsKey(clazz)) {
                Set<T> setInMap = (Set<T>) additionalObjects.get(clazz);
                setInMap.addAll(set);
            } else {
                additionalObjects.put(clazz, set);
            }
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<Set<T>> getAdditionalObjectsFor(Class<T> clazz) {
        checkNotNull(clazz);
        Set<T> set = (Set<T>) additionalObjects.get(clazz);
        if (set != null) {
            return Optional.of(new HashSet<>(set));
        }
        return Optional.empty();
    }
}
