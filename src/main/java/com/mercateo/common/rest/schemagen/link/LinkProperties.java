package com.mercateo.common.rest.schemagen.link;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface LinkProperties {
    Entry[] value();
}
