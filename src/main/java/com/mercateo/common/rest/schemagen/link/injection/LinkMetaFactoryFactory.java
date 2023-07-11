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
import com.mercateo.common.rest.schemagen.link.LinkFactoryContext;
import com.mercateo.common.rest.schemagen.link.LinkMetaFactory;
import jakarta.inject.Inject;
import org.glassfish.hk2.api.Factory;

public class LinkMetaFactoryFactory implements Factory<LinkMetaFactory> {

    private final JsonSchemaGenerator jsonSchemaGenerator;

    private final LinkFactoryContext linkFactoryContext;

    @Inject
    public LinkMetaFactoryFactory(JsonSchemaGenerator jsonSchemaGenerator, LinkFactoryContext linkFactoryContext) {
        this.jsonSchemaGenerator = jsonSchemaGenerator;
        this.linkFactoryContext = linkFactoryContext;
    }

    @Override
    public void dispose(LinkMetaFactory arg0) {
        // nothing
    }

    @Override
    public LinkMetaFactory provide() {
        return LinkMetaFactory.create(jsonSchemaGenerator, linkFactoryContext);
    }
}
