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
package com.mercateo.common.rest.schemagen.link.relation;

import com.google.common.base.Objects;

public class RelationDefault implements Relation {

    private final String name;
    private final RelationType type;

    RelationDefault(String name, RelationType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public RelationType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RelationDefault)) return false;
        RelationDefault that = (RelationDefault) o;
        return Objects.equal(getName(), that.getName()) &&
                Objects.equal(getType(), that.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName(), getType());
    }

    @Override
    public String toString() {
        return "RelationDefault [name=" + name + ", type=" + type + "]";
    }
}
