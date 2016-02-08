package com.mercateo.common.rest.schemagen;

import com.mercateo.common.rest.schemagen.link.LinkFactory;
import com.mercateo.common.rest.schemagen.link.LinkMetaFactory;
import com.mercateo.common.rest.schemagen.link.relation.Rel;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import javax.ws.rs.core.Link;
import java.util.Optional;

public class Benchmarks {

    private static final LinkMetaFactory linkMetaFactory = LinkMetaFactory.createInsecureFactoryForTest();

    private static final LinkFactory<ResourceClass> linkFactory = linkMetaFactory.createFactoryFor(ResourceClass.class);

    @Benchmark
    public static void createLinkFactory() {
        final LinkFactory<ResourceClass> linkFactory = linkMetaFactory.createFactoryFor(ResourceClass.class);
    }

    @Benchmark
    public static void createLink() {
        final Optional<Link> link = linkFactory.forCall(Rel.SELF, r -> r.postSomething(null));
    }

    public static void main(String[] args) throws RunnerException, InterruptedException {

        Options opt = new OptionsBuilder()
                .warmupIterations(10)
                .measurementIterations(10)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
