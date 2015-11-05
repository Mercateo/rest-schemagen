package com.mercateo.common.rest.schemagen.plugin;

import java.lang.reflect.Field;
import java.util.function.BiPredicate;

import com.mercateo.common.rest.schemagen.parameter.CallContext;

/**
 * this class checks, if a field of a bean should be contained in the schema
 * 
 * @author joerg_adler
 *
 */
public interface FieldCheckerForSchema extends BiPredicate<Field, CallContext> {
    static FieldCheckerForSchema fromBiPredicate(BiPredicate<Field, CallContext> predicate) {
        return (f, c) -> predicate.test(f, c);
    }
}
