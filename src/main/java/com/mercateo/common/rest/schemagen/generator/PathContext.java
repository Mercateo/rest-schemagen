package com.mercateo.common.rest.schemagen.generator;

import java.util.HashMap;
import java.util.Map;

public class PathContext {
    private final Map<Class<?>, String> knownTypes;

    private final String currentPath;

    public PathContext() {
        knownTypes = new HashMap<>();
        currentPath = "";
    }

    public PathContext(PathContext pathContext, String name, Class<?> clazz) {
        knownTypes = pathContext.knownTypes;
        currentPath = pathContext.currentPath + '/' + name;
        if (clazz != null) {
            knownTypes.put(clazz, getCurrentPath());
        }
    }

    public PathContext enter(String name, Class<?> clazz) {
        return new PathContext(this, name, clazz);
    }

    public String getCurrentPath() {
        return currentPath.substring(1);
    }

    public boolean isKnown(Class<?> clazz) {
        return knownTypes.containsKey(clazz);
    }

    public String getPath(Class<?> clazz) {
        return knownTypes.get(clazz);
    }
}
