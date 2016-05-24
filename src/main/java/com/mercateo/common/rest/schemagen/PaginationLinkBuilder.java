package com.mercateo.common.rest.schemagen;

import com.mercateo.common.rest.schemagen.link.relation.Rel;
import com.mercateo.common.rest.schemagen.link.relation.RelationContainer;

import javax.ws.rs.core.Link;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PaginationLinkBuilder {

    private int total;

    private int offset;

    private int limit;

    private PaginationLinkBuilder(int total, int offset, int limit) {
        this.total = total;
        this.offset = offset;
        this.limit = limit;
    }

    public static PaginationLinkBuilder of(int total, int offset, int limit) {
        offset = offset < 0 ? 0 : offset;
        limit = limit < 0 ? 0 : limit;
        total = total < 0 ? 0 : total;
        return new PaginationLinkBuilder(total, offset, limit);
    }

    public List<Link> generateLinks(PaginationLinkCreator linkCreator) {
        if (limit == 0) {
            return new ArrayList<>();
        }

        return Stream.of(
                createSelfLink(linkCreator),
                createNextLink(linkCreator),
                createPrevLink(linkCreator),
                createFirstLink(linkCreator),
                createLastLink(linkCreator))
                .flatMap(e -> e.map(Stream::of).orElse(Stream.empty()))
                .collect(Collectors.toList());
    }

    private Optional<Link> createSelfLink(PaginationLinkCreator linkCreator) {
        return linkCreator.of(Rel.SELF, offset, limit);
    }

    private Optional<Link> createNextLink(PaginationLinkCreator linkCreator) {
        if (total > offset + limit) {
            return linkCreator.of(Rel.NEXT, offset + limit, limit);
        }
        return Optional.empty();
    }

    private Optional<Link> createPrevLink(PaginationLinkCreator linkCreator) {
        if (offset < 1) {
            return Optional.empty();
        }
        final int newOffset = offset - limit < 0 ? 0 : offset - limit;
        return linkCreator.of(Rel.PREV, newOffset, limit);
    }

    private Optional<Link> createFirstLink(PaginationLinkCreator linkCreator) {
        if (offset < 1) {
            return Optional.empty();
        }
        return linkCreator.of(Rel.FIRST, 0, limit);
    }

    private Optional<Link> createLastLink(PaginationLinkCreator linkCreator) {
        int numberOfPages = total / limit;

        if (numberOfPages == 0) {
            return Optional.empty();
        }
        final int offsetForLast;

        if ((total % limit) == 0) {
            offsetForLast = (numberOfPages - 1) * limit;
        } else {
            offsetForLast = numberOfPages * limit;
        }

        return linkCreator.of(Rel.LAST, offsetForLast, limit);
    }

    public interface PaginationLinkCreator {
        Optional<Link> of(RelationContainer rel, int offset, int limit);
    }
}
