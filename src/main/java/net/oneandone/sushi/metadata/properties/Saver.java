/**
 * Copyright 1&1 Internet AG, https://github.com/1and1/
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
package net.oneandone.sushi.metadata.properties;

import net.oneandone.sushi.metadata.Cardinality;
import net.oneandone.sushi.metadata.ComplexType;
import net.oneandone.sushi.metadata.Item;
import net.oneandone.sushi.metadata.SimpleType;
import net.oneandone.sushi.metadata.Type;

import java.util.Collection;
import java.util.Properties;

/**
 * Helper class to write properties. You'll usually not use this class directly, use Type.loadProperties.
 */
public class Saver {
    public static void write(Type type, Object obj, String name, final Properties dest) {
        new Saver(dest).write(type, obj, IO.toExternal(name));
    }

    private final Properties dest;
    
    public Saver(Properties dest) {
        this.dest = dest;
    }

    public void write(Type type, Object obj, String key) {
        if (obj != null) {
            type = type.getSchema().type(obj.getClass());
        }
        writeThis(type, obj, key);
        if (type instanceof ComplexType) {
            ComplexType complex;
            String childKey;
            Collection<?> children;
            int idx;

            complex = (ComplexType) type;
            for (Item item : complex.items()) {
                children = item.get(obj);
                idx = 0;
                for (Object child : children) {
                    childKey = join(key, IO.toExternal(item.getName()));
                    if (item.getCardinality() == Cardinality.SEQUENCE) {
                        childKey = childKey + "[" + Integer.toString(idx++) + "]";
                    }
                    write(item.getType(), child, childKey);
                }
            }
        }
    }

    protected void writeThis(Type type, Object obj, String key) {
        Class<?> clazz;

        if (type instanceof SimpleType) {
            dest.setProperty(key, ((SimpleType) type).valueToString(obj));
        } else if (obj == null) {
            dest.setProperty(key, type.getType().getName());
        } else {
            // instanceof ComplexType
            clazz = obj.getClass();
            if (needsMarker(type, clazz)) {
                dest.setProperty(key, clazz.getName());
            }
        }
    }

    private String join(String first, String second) {
        return first.length() == 0 ? second : first + "/" + second;
    }

    private static boolean needsMarker(Type type, Class<?> clazz) {
        if (!type.getType().equals(clazz)) {
            return true;
        }
        return ((type instanceof ComplexType) && ((ComplexType) type).items().isEmpty());
    }
}
