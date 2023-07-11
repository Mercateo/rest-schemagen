/*
 * Created on 26.05.2015
 *
 * author joerg_adler
 */
package com.mercateo.common.rest.schemagen;

import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

public abstract class GenericResource<ReturnType, BeanParamType> {

    @GET
    @Path("{pp}")
    @Produces("application/json")
    public ReturnType get(@BeanParam BeanParamType param) {
        return getReturnType(param);
    }

    protected abstract ReturnType getReturnType(BeanParamType param);
}
