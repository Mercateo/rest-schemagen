package com.mercateo.common.rest.schemagen.plugin;

import java.util.function.Predicate;

import com.mercateo.common.rest.schemagen.link.ScopeMethod;

/**
 * Checks if a link should be created for that method.
 * 
 * @author joerg_adler
 *
 */
public interface MethodCheckerForLink extends Predicate<ScopeMethod> {

    static MethodCheckerForLink fromPredicate(Predicate<ScopeMethod> predicate) {
        return s -> predicate.test(s);
    }

}
