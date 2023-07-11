/**
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
package com.mercateo.common.rest.schemagen.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PaginatedList<T> {
    public final List<T> members;

    public final int total;

    public final int offset;

    public final int limit;

    @JsonCreator
    public PaginatedList(@JsonProperty("total") int total, @JsonProperty("offset") int offset, @JsonProperty("limit") int limit, @JsonProperty("members") List<T> members) {
        this.total = total;
        this.offset = offset;
        this.limit = limit;
        this.members = members;
    }

    @Override
    public String toString() {
        return "PaginatedRto [total=" + total + ", offset=" + offset + ", limit=" + limit
                + ", members=" + members + "]";
    }

}
