package com.mercateo.common.rest.schemagen.link;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.core.Link;

import com.mercateo.common.rest.schemagen.JerseyResource;
import com.mercateo.common.rest.schemagen.link.helper.InvocationRecorder;
import com.mercateo.common.rest.schemagen.link.helper.MethodInvocation;
import com.mercateo.common.rest.schemagen.link.helper.ProxyFactory;
import com.mercateo.common.rest.schemagen.link.relation.Relation;
import com.mercateo.common.rest.schemagen.link.relation.RelationContainer;
import com.mercateo.common.rest.schemagen.parameter.CallContext;
import com.mercateo.common.rest.schemagen.parameter.Parameter;

public class LinkFactory<T extends JerseyResource> {

    private final List<Scope> scopes;

    private final Class<T> resourceClass;

    private final LinkFactoryContext context;

    private final LinkCreator linkCreator;

    LinkFactory(Class<T> resourceClass, LinkFactoryContext context, List<Scope> scopes) {
        this.scopes = scopes != null ? scopes : new ArrayList<>();
        this.resourceClass = requireNonNull(resourceClass);
        this.context = context;
        this.linkCreator = new LinkCreator(context);
    }

    /**
     * computes the link for the resource method, which is called the last time
     * in the lambda. Please note, that the method has to be non final!
     *
     * @param rel
     *            relation of link
     * @param methodInvocation
     *            lambda with function call
     * @return the specified link or absent, if the user has not the required
     *         permission to call the resource!
     */
    public Optional<Link> forCall(RelationContainer rel, MethodInvocation<T> methodInvocation) {
        return forCall(rel.getRelation(), methodInvocation);
    }

    /**
     * computes the link for the resource method, which is called the last time
     * in the lambda. Please note, that the method has to be non final!
     *
     * @param rel
     *            relation of link
     * @param methodInvocation
     *            lambda with function call
     * @param callContext
     *            parameter container containing information about allowed and
     *            default values
     * @return the specified link or absent, if the user has not the required
     *         permission to call the resource!
     */
    public Optional<Link> forCall(RelationContainer rel, MethodInvocation<T> methodInvocation,
            CallContext callContext) {
        return forCall(rel.getRelation(), methodInvocation, callContext);
    }

    /**
     * computes the link for the resource method, which is called the last time
     * in the lambda. Please note, that the method has to be non final!
     *
     * @param relation
     *            relation of link
     * @param methodInvocation
     *            lambda with function call
     * @return the specified link or absent, if the user has not the required
     *         permission to call the resource!
     */
    public Optional<Link> forCall(Relation relation, MethodInvocation<T> methodInvocation) {
        return forCall(relation, methodInvocation, Parameter.createContext());
    }

    /**
     * computes the link for the resource method, which is called the last time
     * in the lambda. Please note, that the method has to be non final!
     * 
     * @param relation
     *            relation of link
     * @param methodInvocation
     *            lambda with function call
     * @param callContext
     *            parameter container containing information about allowed and
     *            default values
     * @return the specified link or absent, if the user has not the required
     *         permission to call the resource!
     */
    public Optional<Link> forCall(Relation relation, MethodInvocation<T> methodInvocation,
            CallContext callContext) {
        final List<Scope> scopes = new ArrayList<>();
        scopes.addAll(this.scopes);
        scopes.add(new Scope(resourceClass, methodInvocation));

        final List<InvocationRecorder> invocationRecorders = scopes.stream().map(
                this::scopeToInvocationRecorder).collect(Collectors.toList());

        if (invocationRecorders.stream().allMatch(this::withAllPermissions)) {
            final List<ScopeMethod> scopeMethods = invocationRecorders.stream().map(
                    x -> invocationRecorderToScopeMethod(x, callContext)).collect(Collectors
                            .toList());
            return Optional.of(linkCreator.createFor(scopeMethods, relation));
        }
        return Optional.empty();
    }

    public <U extends JerseyResource> LinkFactory<U> subResource(
            MethodInvocation<T> methodInvocation, Class<U> subResourceClass) {
        return new LinkFactory<>(subResourceClass, context, new ArrayList<Scope>() {
            private static final long serialVersionUID = -9144825887185625018L;

            {
                addAll(scopes);
                add(new Scope(LinkFactory.this.resourceClass, methodInvocation));
            }
        });
    }

    private ScopeMethod invocationRecorderToScopeMethod(InvocationRecorder invocationRecorder,
            CallContext context) {
        return new ScopeMethod(invocationRecorder.getInvocationRecordingResult(), context);
    }

    private boolean withAllPermissions(InvocationRecorder invocationRecorder) {
        ScopeMethod scopeMethod = new ScopeMethod(invocationRecorder
                .getInvocationRecordingResult());
        return context == null || context.getMethodCheckerForLink().test(scopeMethod);
    }

    @SuppressWarnings("unchecked")
    private <U extends JerseyResource> InvocationRecorder scopeToInvocationRecorder(Scope scope) {
        // we know that ResourceClass and MethodInvocation match each other, so
        // the casts are safe
        final Class<U> clazz = (Class<U>) scope.getResourceClass();
        final MethodInvocation<U> methodInvocation = (MethodInvocation<U>) scope
                .getMethodInvocation();

        U t = ProxyFactory.createProxy(clazz);
        methodInvocation.record(t);
        return (InvocationRecorder) t;
    }

    static class Scope {
        private final Class<? extends JerseyResource> resourceClass;

        private final MethodInvocation<? extends JerseyResource> lambda;

        public <T extends JerseyResource> Scope(Class<T> resourceClass,
                MethodInvocation<T> lambda) {
            this.resourceClass = requireNonNull(resourceClass);
            this.lambda = requireNonNull(lambda);
        }

        public Class<? extends JerseyResource> getResourceClass() {
            return resourceClass;
        }

        public MethodInvocation<? extends JerseyResource> getMethodInvocation() {
            return lambda;
        }
    }
}
