package com.mercateo.common.rest.schemagen.link;

import com.mercateo.common.rest.schemagen.JerseyResource;
import com.mercateo.common.rest.schemagen.JsonSchemaGenerator;
import com.mercateo.common.rest.schemagen.link.helper.MethodInvocation;
import com.mercateo.common.rest.schemagen.link.relation.Relation;
import com.mercateo.common.rest.schemagen.link.relation.RelationContainer;
import com.mercateo.common.rest.schemagen.parameter.CallContext;
import com.mercateo.common.rest.schemagen.parameter.Parameter;
import com.mercateo.reflection.Call;
import com.mercateo.reflection.InvocationRecorder;
import com.mercateo.reflection.proxy.ProxyCache;

import javax.ws.rs.core.Link;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LinkFactory<T extends JerseyResource> {

    private static final ProxyCache PROXY_CACHE = new ProxyCache();

    private final List<Scope> scopes;

    private final JsonSchemaGenerator jsonSchemaGenerator;

    private final LinkFactoryContext context;

    private final LinkCreator linkCreator;

    private final T resourceProxy;

    LinkFactory(Class<T> resourceClass, JsonSchemaGenerator jsonSchemaGenerator, LinkFactoryContext context, List<Scope> scopes) {
        resourceProxy = PROXY_CACHE.createProxy(resourceClass);
        this.jsonSchemaGenerator = jsonSchemaGenerator;
        this.context = context;
        this.scopes = scopes != null ? scopes : new ArrayList<>();
        this.linkCreator = new LinkCreator(jsonSchemaGenerator, context);
    }

    /**
     * computes the link for the resource method, which is called the last time
     * in the lambda. Please note, that the method has to be non final!
     *
     * @param rel                relation of link
     * @param methodInvocation   lambda with function call
     * @param linkFactoryContext request scoped link context
     * @return the specified link or absent, if the user has not the required
     * permission to call the resource!
     */
    public Optional<Link> forCall(RelationContainer rel, MethodInvocation<T> methodInvocation, LinkFactoryContext linkFactoryContext) {
        return forCall(rel.getRelation(), methodInvocation, linkFactoryContext);
    }

    /**
     * computes the link for the resource method, which is called the last time
     * in the lambda. Please note, that the method has to be non final!
     *
     * @param rel              relation of link
     * @param methodInvocation lambda with function call
     * @return the specified link or absent, if the user has not the required
     * permission to call the resource!
     */
    public Optional<Link> forCall(RelationContainer rel, MethodInvocation<T> methodInvocation) {
        return forCall(rel.getRelation(), methodInvocation);
    }

    /**
     * computes the link for the resource method, which is called the last time
     * in the lambda. Please note, that the method has to be non final!
     *
     * @param rel                relation of link
     * @param methodInvocation   lambda with function call
     * @param callContext        parameter container containing information about allowed and
     *                           default values
     * @param linkFactoryContext request scoped link context
     * @return the specified link or absent, if the user has not the required
     * permission to call the resource!
     */
    public Optional<Link> forCall(RelationContainer rel, MethodInvocation<T> methodInvocation,
                                  CallContext callContext, LinkFactoryContext linkFactoryContext) {
        return forCall(rel.getRelation(), methodInvocation, callContext, linkFactoryContext);
    }

    /**
     * computes the link for the resource method, which is called the last time
     * in the lambda. Please note, that the method has to be non final!
     *
     * @param rel              relation of link
     * @param methodInvocation lambda with function call
     * @param callContext      parameter container containing information about allowed and
     *                         default values
     * @return the specified link or absent, if the user has not the required
     * permission to call the resource!
     */
    public Optional<Link> forCall(RelationContainer rel, MethodInvocation<T> methodInvocation,
                                  CallContext callContext) {
        return forCall(rel.getRelation(), methodInvocation, callContext);
    }

    /**
     * computes the link for the resource method, which is called the last time
     * in the lambda. Please note, that the method has to be non final!
     *
     * @param relation           relation of link
     * @param methodInvocation   lambda with function call
     * @param linkFactoryContext request scoped link context
     * @return the specified link or absent, if the user has not the required
     * permission to call the resource!
     */
    public Optional<Link> forCall(Relation relation, MethodInvocation<T> methodInvocation, LinkFactoryContext linkFactoryContext) {
        return forCall(relation, methodInvocation, Parameter.createContext(), linkFactoryContext);
    }

    /**
     * computes the link for the resource method, which is called the last time
     * in the lambda. Please note, that the method has to be non final!
     *
     * @param relation         relation of link
     * @param methodInvocation lambda with function call
     * @return the specified link or absent, if the user has not the required
     * permission to call the resource!
     */
    public Optional<Link> forCall(Relation relation, MethodInvocation<T> methodInvocation) {
        return forCall(relation, methodInvocation, Parameter.createContext());
    }

    /**
     * computes the link for the resource method, which is called the last time
     * in the lambda. Please note, that the method has to be non final!
     *
     * @param relation         relation of link
     * @param methodInvocation lambda with function call
     * @param callContext      parameter container containing information about allowed and
     *                         default values
     * @return the specified link or absent, if the user has not the required
     * permission to call the resource!
     */
    public Optional<Link> forCall(Relation relation, MethodInvocation<T> methodInvocation,
                                  CallContext callContext) {
        final List<Scope> scopes = createScopes(methodInvocation, callContext);

        if (scopes.stream().allMatch(scope -> hasAllPermissions(context, scope))) {
            return Optional.of(linkCreator.createFor(scopes, relation));
        }
        return Optional.empty();
    }

    /**
     * computes the link for the resource method, which is called the last time
     * in the lambda. Please note, that the method has to be non final!
     *
     * @param relation         relation of link
     * @param methodInvocation lambda with function call
     * @param callContext      parameter container containing information about allowed and
     *                         default values
     * @param linkFactoryContext baseUri and field and method checkers
     * @return the specified link or absent, if the user has not the required
     * permission to call the resource!
     */
    public Optional<Link> forCall(Relation relation, MethodInvocation<T> methodInvocation, CallContext callContext, LinkFactoryContext linkFactoryContext) {
        final List<Scope> scopes = createScopes(methodInvocation, callContext);

        if (scopes.stream().allMatch(scope -> hasAllPermissions(linkFactoryContext, scope))) {
            return Optional.of(linkCreator.createFor(scopes, relation, linkFactoryContext));
        }
        return Optional.empty();
    }

    private List<Scope> createScopes(MethodInvocation<T> methodInvocation, CallContext callContext) {
        final List<Scope> scopes = new ArrayList<>(this.scopes);
        scopes.add(createCallScope(methodInvocation, callContext));
        return scopes;
    }

    public <U extends JerseyResource> LinkFactory<U> subResource(
            MethodInvocation<T> methodInvocation, Class<U> subResourceClass) {
        return new LinkFactory<>(subResourceClass, jsonSchemaGenerator, context, new ArrayList<Scope>(scopes) {
            private static final long serialVersionUID = -9144825887185625018L;

            {
                add(createSubresourceScope(methodInvocation));
            }
        });
    }

    private Scope createCallScope(MethodInvocation<T> methodInvocation, CallContext callContext) {
        final Call<T> result = recordMethodCall(methodInvocation);
        return new CallScope(result.declaringClass(), result.method(), result.args(), callContext);
    }

    private Scope createSubresourceScope(MethodInvocation<T> methodInvocation) {
        final Call<T> result = recordMethodCall(methodInvocation);
        return new SubResourceScope(result.declaringClass(), result.method(), result.args());
    }

    private Call<T> recordMethodCall(MethodInvocation<T> methodInvocation) {
        final Call<T> result;
        synchronized (resourceProxy) {
            methodInvocation.record(resourceProxy);
            //noinspection unchecked
            result = ((InvocationRecorder<T>) resourceProxy).getInvocationRecordingResult();
        }
        return result;
    }

    private boolean hasAllPermissions(LinkFactoryContext context, Scope scope) {
        return context == null || context.getMethodCheckerForLink().test(scope);
    }

}
