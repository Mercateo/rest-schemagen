package com.mercateo.common.rest.schemagen.plugin.common;

import com.mercateo.common.rest.schemagen.plugin.TargetSchemaEnablerForLink;

import org.glassfish.hk2.api.Factory;

public class TargetSchemaEnablerForLinkFactory implements Factory<TargetSchemaEnablerForLink> {
    @Override
    public TargetSchemaEnablerForLink provide() {
        return TargetSchemaEnablerForLink.fromPredicate(scope -> true);
    }

    @Override
    public void dispose(TargetSchemaEnablerForLink targetSchemaEnablerForLink) {
        // nothing

    }
}
