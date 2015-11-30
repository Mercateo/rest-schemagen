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
