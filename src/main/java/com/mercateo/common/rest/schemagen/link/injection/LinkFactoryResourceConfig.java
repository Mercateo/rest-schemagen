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
package com.mercateo.common.rest.schemagen.link.injection;

import com.mercateo.common.rest.schemagen.JsonSchemaGenerator;
import com.mercateo.common.rest.schemagen.RestJsonSchemaGeneratorFactory;
import com.mercateo.common.rest.schemagen.link.LinkFactoryContext;
import com.mercateo.common.rest.schemagen.link.LinkMetaFactory;
import com.mercateo.common.rest.schemagen.link.helper.BaseUriCreator;
import com.mercateo.common.rest.schemagen.link.helper.BaseUriCreatorDefault;
import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;
import com.mercateo.common.rest.schemagen.plugin.MethodCheckerForLink;
import com.mercateo.common.rest.schemagen.plugin.TargetSchemaEnablerForLink;
import com.mercateo.common.rest.schemagen.plugin.common.FieldCheckerForSchemaFactory;
import com.mercateo.common.rest.schemagen.plugin.common.MethodCheckerForLinkFactory;
import com.mercateo.common.rest.schemagen.plugin.common.TargetSchemaEnablerForLinkFactory;

import javax.inject.Singleton;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;

public class LinkFactoryResourceConfig {

    public static void configureWithoutPlugins(ResourceConfig config) {
        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(RestJsonSchemaGeneratorFactory.class, Singleton.class)
                        .to(JsonSchemaGenerator.class)
                        .in(Singleton.class);
                bind(BaseUriCreatorDefault.class)
                        .to(BaseUriCreator.class)
                        .in(Singleton.class);
                bindFactory(LinkFactoryContextFactory.class)
                        .to(LinkFactoryContext.class)
                        .in(RequestScoped.class)
                        .proxy(true);
                bindFactory(LinkMetaFactoryFactory.class)
                        .to(LinkMetaFactory.class);
            }
        });

    }

    private static void bindDefaultPlugins(ResourceConfig config) {
        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(MethodCheckerForLinkFactory.class)
                        .to(MethodCheckerForLink.class)
                        .in(RequestScoped.class)
                        .proxy(true);
                bindFactory(TargetSchemaEnablerForLinkFactory.class)
                        .to(TargetSchemaEnablerForLink.class)
                        .in(RequestScoped.class)
                        .proxy(true);
                bindFactory(FieldCheckerForSchemaFactory.class, Singleton.class)
                        .to(FieldCheckerForSchema.class)
                        .in(Singleton.class);
            }
        });
    }

    public static void configure(ResourceConfig resourceConfig) {
        configureWithoutPlugins(resourceConfig);
        bindDefaultPlugins(resourceConfig);
    }
}
