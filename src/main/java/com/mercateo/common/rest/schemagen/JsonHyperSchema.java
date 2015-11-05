package com.mercateo.common.rest.schemagen;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.annotations.VisibleForTesting;
import com.mercateo.common.rest.schemagen.link.helper.JsonLink;
import com.mercateo.common.rest.schemagen.link.relation.RelationContainer;

public class JsonHyperSchema {

    @XmlJavaTypeAdapter(JaxbAdapter.class)
    private List<Link> links;

    public JsonHyperSchema(List<Link> links) {
        this.links = links;
    }

    public List<Link> getLinks() {
        return links;
    }

    @VisibleForTesting
    public Optional<Link> getByRel(RelationContainer rel) {
        return links.stream().filter(e -> e.getRel().equals(rel.getRelation().getName()))
                .findFirst();
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public static class JaxbAdapter extends XmlAdapter<JsonLink, Link> {
        @Override
        public Link unmarshal(JsonLink v) {
            Link.Builder lb = Link.fromUri(v.getHref());
            if (v.getMap() != null) {
                for (Entry<String, String> en : v.getMap().entrySet()) {
                    lb.param(en.getKey(), en.getValue());
                }
            }
            lb.rel(v.getMap().get("rel"));
            return lb.build();
        }

        @Override
        public JsonLink marshal(Link v) {
            try {
                return new JsonLink(v);
            } catch (IOException e) {
                throw new ProcessingException(e);
            }
        }
    }

    public static JsonHyperSchema from(Link... links) {
        return new JsonHyperSchema(Arrays.stream(links).filter(link -> link != null).collect(
                Collectors.toList()));
    }

    @SafeVarargs
    public static JsonHyperSchema from(Optional<Link>... links) {
        return new JsonHyperSchema(filterOptionals(Arrays.stream(links)).collect(Collectors
                .toList()));
    }

    public static JsonHyperSchema from(Collection<Link> links) {
        return new JsonHyperSchema(links.stream().filter(link -> link != null).collect(Collectors
                .toList()));
    }

    public static JsonHyperSchema fromOptional(Collection<Optional<Link>> links) {
        return new JsonHyperSchema(filterOptionals(links.stream()).collect(Collectors.toList()));
    }

    private static <T> Stream<T> filterOptionals(Stream<Optional<T>> streamWithOptionals) {
        return streamWithOptionals.filter(Optional::isPresent).map(Optional::get);
    }
}