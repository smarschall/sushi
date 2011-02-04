/*
 * Copyright 1&1 Internet AG, http://www.1and1.org
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.oneandone.sushi.metadata.simpletypes;

import com.oneandone.sushi.metadata.Schema;
import com.oneandone.sushi.metadata.SimpleType;

public class StringType extends SimpleType {
    public StringType(Schema schema) {
        super(schema, String.class, "string");
    }

    @Override
    public Object newInstance() {
        return "";
    }

    @Override
    public String valueToString(Object obj) {
        return (String) obj;
    }
    
    @Override
    public Object stringToValue(String str) {
        return str;
    }
}