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