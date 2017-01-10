package com.mercateo.common.rest.schemagen.plugin;

import java.util.function.Predicate;

import com.mercateo.common.rest.schemagen.link.Scope;

/**
 * Checks if a link should be created for that method.
 * 
 * @author joerg_adler
 *
 */
public interface MethodCheckerForLink extends Predicate<Scope> {

    static MethodCheckerForLink fromPredicate(Predicate<Scope> predicate) {
        return predicate::test;
    }
}
