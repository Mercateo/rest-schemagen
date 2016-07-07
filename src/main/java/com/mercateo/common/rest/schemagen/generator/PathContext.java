package com.mercateo.common.rest.schemagen.generator;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class PathContext {
    private final Map<Type, String> knownTypes;

    private final String currentPath;

    public PathContext() {
        knownTypes = new HashMap<>();
        currentPath = "";
    }

    public PathContext(PathContext pathContext, String name, Type type) {
        knownTypes = pathContext.knownTypes;
        currentPath = pathContext.currentPath + '/' + name;
        if (type != null) {
            knownTypes.put(type, getCurrentPath());
        }
    }

    public PathContext enter(String name, Type type) {
        return new PathContext(this, name, type);
    }

    public String getCurrentPath() {
        return currentPath.substring(1);
    }

    public boolean isKnown(Type type) {
        return knownTypes.containsKey(type);
    }

    public String getPath(Type type) {
        return knownTypes.get(type);
    }
}
