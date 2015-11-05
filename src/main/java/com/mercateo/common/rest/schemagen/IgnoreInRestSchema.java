/*
 * Created on Feb 10, 2015
 *
 * author alexei
 */
package com.mercateo.common.rest.schemagen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreInRestSchema {
    // nothing
}