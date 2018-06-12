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
package com.mercateo.common.rest.schemagen.link.relation;

public interface Relation {

    RelationType DEFAULT_TYPE = RelType.SELF.getRelationType();

    String getName();

    RelationType getType();

    static <E extends Enum<E> & RelationContainer> Relation of(E relation) {
        return of(relation, DEFAULT_TYPE);
    }

    static <E extends Enum<E> & RelationContainer> Relation of(E relation, RelationTypeContainer typeContainer) {
        return of(relation, typeContainer.getRelationType());
    }

    static <E extends Enum<E> & RelationContainer> Relation of(E relation, RelationType type) {
        return of(relation.name().toLowerCase().replace('_', '-'), type);
    }

    static Relation of(String name, RelationTypeContainer typeContainer) {
        return of(name, typeContainer.getRelationType());
    }

    static Relation of(String name) {
        return new RelationDefault(name, DEFAULT_TYPE);
    }

    static Relation of(String name, RelationType type) {
        return new RelationDefault(name, type);
    }
}
