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
