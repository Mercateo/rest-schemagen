/*
 * Created on 26.05.2015
 *
 * author joerg_adler
 */
package com.mercateo.common.rest.schemagen;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

public abstract class GenericResource<ReturnType, BeanParamType> {

    @GET
    @Path("{pp}")
    public ReturnType get(@BeanParam BeanParamType param) {
        return getReturnType(param);
    }

    protected abstract ReturnType getReturnType(BeanParamType param);
}
