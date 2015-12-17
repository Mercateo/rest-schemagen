package com.mercateo.common.rest.schemagen.plugin.common;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonView;
import com.mercateo.common.rest.schemagen.parameter.CallContext;
import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;

public class JsonViewChecker implements FieldCheckerForSchema {

    @Override
    public boolean test(Field field, CallContext context) {
        checkNotNull(field);
        checkNotNull(context);
        final JsonView jsonView = field.getAnnotation(JsonView.class);
        if (jsonView != null) {
            @SuppressWarnings("rawtypes")
            Optional<Set<Class>> viewClasses = context.getAdditionalObjectsFor(Class.class);
            return !viewClasses.isPresent() || Arrays.stream(jsonView.value()).anyMatch(c->viewClasses.get().contains(c));
        }
        return true;
    }

}
