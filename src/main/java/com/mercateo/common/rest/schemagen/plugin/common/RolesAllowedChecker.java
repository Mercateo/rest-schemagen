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

import static java.util.Objects.requireNonNull;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.core.SecurityContext;

import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.server.model.AnnotatedMethod;

import com.mercateo.common.rest.schemagen.link.Scope;
import com.mercateo.common.rest.schemagen.plugin.MethodCheckerForLink;

/**
 * copied from {@link RolesAllowedDynamicFeature}
 * 
 * @author joerg.adler
 *
 */
public class RolesAllowedChecker implements MethodCheckerForLink {

    private SecurityContext securityContext;

    public RolesAllowedChecker(SecurityContext securityContext) {
        this.securityContext = requireNonNull(securityContext);
    }

    @Override
    public boolean test(Scope scope) {

        AnnotatedMethod am = new AnnotatedMethod(scope.getInvokedMethod());

        // DenyAll on the method take precedence over RolesAllowed and PermitAll
        if (am.isAnnotationPresent(DenyAll.class)) {
            return false;
        }

        // RolesAllowed on the method takes precedence over PermitAll
        RolesAllowed ra = am.getAnnotation(RolesAllowed.class);
        if (ra != null) {
            return checkRoles(ra.value());
        }

        // PermitAll takes precedence over RolesAllowed on the class
        if (am.isAnnotationPresent(PermitAll.class)) {
            // Do nothing.
            return true;
        }

        // DenyAll can't be attached to classes

        // RolesAllowed on the class takes precedence over PermitAll
        ra = scope.getInvokedClass().getAnnotation(RolesAllowed.class);
        if (ra != null) {
            return checkRoles(ra.value());
        }
        return true;
    }

    private boolean checkRoles(String[] rolesAllowed) {
        for (String role : rolesAllowed) {
            if (securityContext.isUserInRole(role)) {
                return true;
            }
        }
        return false;
    }
}
