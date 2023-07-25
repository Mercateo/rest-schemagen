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

import java.util.Arrays;
import java.util.List;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

public class ImplementedBeanParamType {

    @QueryParam("qp1")
    @DefaultValue("default")
    private String queryParam1;

    @QueryParam("qp2")
    private String queryParam2;

    @QueryParam("elements")
    private List<String> elements;

    @PathParam("pp")
    private String pathParam;

    public String getQueryParam() {
        return queryParam1;
    }

    public void setQueryParam1(String queryParam1) {
        this.queryParam1 = queryParam1;
    }

    public String getQueryParam2() {
        return queryParam2;
    }

    public void setQueryParam2(String queryParam2) {
        this.queryParam2 = queryParam2;
    }

    public String getPathParam() {
        return pathParam;
    }

    public void setPathParam(String pathParam) {
        this.pathParam = pathParam;
    }

    public void setElements(String... elements) {
        this.elements = Arrays.asList(elements);
    }
}