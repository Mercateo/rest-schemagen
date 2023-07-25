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
package com.mercateo.common.rest.schemagen.link;

import static java.util.Objects.requireNonNull;

import java.net.URI;

import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;
import com.mercateo.common.rest.schemagen.plugin.MethodCheckerForLink;
import com.mercateo.common.rest.schemagen.plugin.TargetSchemaEnablerForLink;

public class LinkFactoryContextDefault implements LinkFactoryContext {

    private final URI baseUri;

    private final MethodCheckerForLink methodCheckerForLink;

    private final FieldCheckerForSchema fieldCheckerForSchema;

    private final TargetSchemaEnablerForLink targetSchemaEnablerForLink;

    public LinkFactoryContextDefault() {
        baseUri = null;
        methodCheckerForLink = null;
        fieldCheckerForSchema = null;
        targetSchemaEnablerForLink = null;
    }

    public LinkFactoryContextDefault(URI baseUri, MethodCheckerForLink methodCheckerForLink,
            FieldCheckerForSchema fieldCheckerForSchema, TargetSchemaEnablerForLink targetSchemaEnablerForLink) {
        this.baseUri = baseUri;
        this.methodCheckerForLink = requireNonNull(methodCheckerForLink);
        this.fieldCheckerForSchema = requireNonNull(fieldCheckerForSchema);
        this.targetSchemaEnablerForLink = targetSchemaEnablerForLink;
    }

    @Override
    public URI getBaseUri() {
        return baseUri;
    }

    @Override
    public FieldCheckerForSchema getFieldCheckerForSchema() {
        return fieldCheckerForSchema;
    }

    @Override
    public MethodCheckerForLink getMethodCheckerForLink() {
        return methodCheckerForLink;
    }

    @Override
    public TargetSchemaEnablerForLink getTargetSchemaEnablerForLink() {
        return targetSchemaEnablerForLink;
    }
}
