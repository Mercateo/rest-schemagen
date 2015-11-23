package com.mercateo.common.rest.schemagen;

import javax.validation.constraints.Size;

@SuppressWarnings("unused")
public class NegativeSizeConstraintRto {

    @Size(min = -4)
    private String negativeSizeString;
}
