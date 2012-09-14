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
package net.sf.beezle.sushi.metadata.store;

import net.sf.beezle.sushi.metadata.Cardinality;
import net.sf.beezle.sushi.metadata.ComplexType;
import net.sf.beezle.sushi.metadata.Item;
import net.sf.beezle.sushi.metadata.SimpleType;
import net.sf.beezle.sushi.metadata.Type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Helper class to read and write properties. You'll usually not use this class directly,
 * use Type.loadProperties and Data.toProperties instead. 
 */
public class Writer {
    public static void write(Type type, Object obj, String name, final Store dest) {
        new Writer(dest).write(new ArrayList<Item<?>>(), type, obj, name);
    }

    private final Store dest;
    
    public Writer(Store dest) {
        this.dest = dest;
    }

    public Type write(List<Item<?>> parents, Type type, Object obj, String path) {
        if (obj != null) {
            type = type.getSchema().type(obj.getClass());
        }
        writeThis(parents, type, obj, path);
        if (type instanceof ComplexType) {
            ComplexType complex;
            String childName;
            Collection<?> children;
            int idx;

            complex = (ComplexType) type;
            for (Item item : complex.items()) {
                children = item.get(obj);
                parents.add(item);
                idx = 0;
                for (Object child : children) {
                    childName = join(path, item.getName());
                    if (item.getCardinality() == Cardinality.SEQUENCE) {
                        childName = childName + "[" + Integer.toString(idx++) + "]";
                    }
                    write(parents, item.getType(), child, childName);
                }
                parents.remove(parents.size() - 1);
            }
        }
        return type;
    }

    protected void writeThis(List<Item<?>> parents, Type type, Object obj, String path) {
        String value;
        
        if (type instanceof SimpleType) {
            value = ((SimpleType) type).valueToString(obj);
        } else if (obj == null) {
            value = type.getType().getName();
        } else {
            value = obj.getClass().getName(); 
        }
        try {
            dest.write(parents, path, value);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new StoreException(path + ": write failed: " + e.getMessage(), e);
        }
    }

    private String join(String first, String second) {
        return first.length() == 0 ? second : first + "/" + second;
    }
}
