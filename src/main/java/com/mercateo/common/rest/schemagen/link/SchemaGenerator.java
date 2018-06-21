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
package com.mercateo.common.rest.schemagen.link;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.Link;

import com.mercateo.common.rest.schemagen.JerseyResource;
import com.mercateo.common.rest.schemagen.link.helper.MethodInvocation;
import com.mercateo.common.rest.schemagen.link.relation.RelationContainer;

public class SchemaGenerator<T extends JerseyResource> {
    private LinkFactory<T> linkFactory;

    private final List<Optional<Link>> links;

    public static <T extends JerseyResource> SchemaGenerator<T> builder(
            LinkFactory<T> linkFactory) {
        return new SchemaGenerator<T>(linkFactory, new ArrayList<>());
    }

    public SchemaGenerator(
            LinkFactory<T> linkFactory, List<Optional<Link>> links) {
        this.linkFactory = linkFactory;
        this.links = links;
    }

    public SchemaGenerator<T> withLink(RelationContainer rel,
            MethodInvocation<T> methodInvocation) {
        links.add(linkFactory.forCall(rel, methodInvocation));
        return this;
    }

    public <V extends JerseyResource> SchemaGenerator<V> withFactory(LinkFactory<V> linkFactory) {
        return new SchemaGenerator<V>(linkFactory, links);
    }

    public List<Optional<Link>> build() {
        return links;
    }
}