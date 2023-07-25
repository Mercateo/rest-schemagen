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

public class RelationTypeDefault implements RelationType {
    private final String name;

    private final boolean shouldBeSerialized;

    private final String serializedName;

    public RelationTypeDefault(String name, boolean shouldBeSerialized, String serializedName) {
        this.name = name;
        this.shouldBeSerialized = shouldBeSerialized;
        this.serializedName = serializedName;
    }

    @Override
    public boolean isShouldBeSerialized() {
        return shouldBeSerialized;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }

    @SuppressWarnings("boxing")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RelationTypeDefault)) return false;
        RelationTypeDefault that = (RelationTypeDefault) o;
        return Objects.equal(isShouldBeSerialized(), that.isShouldBeSerialized()) &&
                Objects.equal(getName(), that.getName()) &&
                Objects.equal(getSerializedName(), that.getSerializedName());
    }

    @SuppressWarnings("boxing")
    @Override
    public int hashCode() {
        return Objects.hashCode(getName(), isShouldBeSerialized(), getSerializedName());
    }
}
