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
package com.mercateo.common.rest.schemagen;

import java.util.Optional;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.mercateo.common.rest.schemagen.link.LinkFactory;
import com.mercateo.common.rest.schemagen.link.LinkMetaFactory;
import com.mercateo.common.rest.schemagen.link.relation.Rel;

import jakarta.ws.rs.core.Link;

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
