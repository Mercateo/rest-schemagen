package com.mercateo.common.rest.schemagen.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertySchema {
    Class<? extends IndividualSchemaGenerator>schemaGenerator();
}
