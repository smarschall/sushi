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

package de.ui.sushi.fs;

import java.util.regex.Pattern;

public class LineFormat {
    // order is important
    public static final Pattern GENERIC_SEPARATOR = Pattern.compile(Pattern.quote("\n\r") + "|" + Pattern.quote("\r\n")  + "|" + Pattern.quote("\n") + "|" + Pattern.quote("\r"));

    public static final LineFormat GENERIC_FORMAT = new LineFormat(GENERIC_SEPARATOR, Trim.SEPARATOR, true, null, 1);

    public static enum Trim {
        NOTHING, SEPARATOR, ALL
    }

    /** line separator */
    public final Pattern separator;

    /** line trimming mode */
    public final Trim trim;

    /** when true, next() returns empty line; otherwise, they're skipped */
    public final boolean empty;

    /** line comment prefix to be skipped; null to disable */
    public final String comment;

    public final int firstLine;

    public LineFormat(Pattern separator, Trim trim, boolean empty, String comment, int firstLine) {
        this.separator = separator;
        this.trim = trim;
        this.empty = empty;
        this.comment = comment;
        this.firstLine = firstLine;
    }
}
