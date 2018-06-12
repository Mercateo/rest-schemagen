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
package com.mercateo.common.rest.schemagen.plugin.common;

import javax.inject.Inject;
import javax.ws.rs.core.SecurityContext;

import org.glassfish.hk2.api.Factory;

import com.mercateo.common.rest.schemagen.plugin.MethodCheckerForLink;

public class MethodCheckerForLinkFactory implements Factory<MethodCheckerForLink> {

    private SecurityContext securityContext;

    @Inject
    public MethodCheckerForLinkFactory(SecurityContext securityContext) {
        this.securityContext = securityContext;
    }

    @Override
    public MethodCheckerForLink provide() {
        return new RolesAllowedChecker(securityContext);
    }

    @Override
    public void dispose(MethodCheckerForLink instance) {
        // nothing
    }

}
