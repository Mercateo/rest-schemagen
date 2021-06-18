package com.mercateo.common.rest.schemagen.plugin;

import com.mercateo.common.rest.schemagen.link.Scope;

import java.util.function.Predicate;

public interface TargetSchemaEnablerForLink extends Predicate<Scope> {

    static TargetSchemaEnablerForLink fromPredicate(Predicate<Scope> predicate) {
        return predicate::test;
    }
}
