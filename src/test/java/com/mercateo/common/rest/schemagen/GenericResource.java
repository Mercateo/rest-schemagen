/*
 * Copyright © 2015 Mercateo AG (http://www.mercateo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
