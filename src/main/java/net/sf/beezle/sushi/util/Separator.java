package net.sf.beezle.sushi.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Splits and joins strings on a separator. Similar to Google's Splitter
 * http://guava-libraries.googlecode.com/svn/tags/release09/javadoc/com/google/common/base/Splitter.html and
 * Joiner http://guava-libraries.googlecode.com/svn/tags/release09/javadoc/com/google/common/base/Joiner.html.
 * Immutable, configuration methods return new instances.
 */
public class Separator {
    /** Whitespace delimited lists. Skip empty because I want to ignore heading/tailing whitespace */
    public static final Separator SPACE = Separator.on(" ", Pattern.compile("\\s+", Pattern.MULTILINE)).skipEmpty();

    /** Separator in user-supplied lists. */
    public static final Separator LIST = Separator.on(',').trim().skipEmpty().forNull("null");

    public static Separator on(char c) {
        return on(Character.toString(c));
    }

    public static Separator on(String separator) {
        return new Separator(separator, Pattern.compile(separator, Pattern.MULTILINE | Pattern.LITERAL));
    }

    public static Separator on(String separator, Pattern pattern) {
        return new Separator(separator, pattern);
    }

    //--

    private final String separator;
    private final Pattern pattern;
    private final boolean trim;
    private final boolean skipEmpty;
    private final String forNull;
    private final boolean skipNull;

    public Separator(String separator, Pattern pattern) {
        this(separator, pattern, false, false, null, false);
    }

    public Separator(String separator, Pattern pattern, boolean trim, boolean skipEmpty, String forNull, boolean skipNull) {
        if (pattern.matcher("").find()) {
            throw new IllegalArgumentException(pattern.pattern() + " matches the empty string");
        }
        this.separator = separator;
        this.pattern = pattern;
        this.trim = trim;
        this.skipEmpty = skipEmpty;
        this.forNull = forNull;
        this.skipNull = skipNull;
    }

    public Separator(Separator orig) {
        this(orig.separator, orig.pattern, orig.trim, orig.skipEmpty, orig.forNull, orig.skipNull);
    }

    //--

    public String getSeparator() {
        return separator;
    }

    //-- configuration

    /** Trim elements before joining or after splitting */
    public Separator trim() {
        return new Separator(separator, pattern, true, skipEmpty, forNull, skipNull);
    }

    /** Do not join empty elements and to not return them from splitting. */
    public Separator skipEmpty() {
        return new Separator(separator, pattern, trim, true, forNull, skipNull);
    }

    public Separator forNull(String forNull) {
        return new Separator(separator, pattern, trim, skipEmpty, forNull, skipNull);
    }

    public Separator skipNull() {
        return new Separator(separator, pattern, trim, skipEmpty, forNull, true);
    }

    //-- joining

    public String join(Object[] array) {
        return join(java.util.Arrays.asList(array));
    }

    public String join(Iterable<?> lst) {
        StringBuilder result;

        result = new StringBuilder();
        appendTo(result, lst);
        return result.toString();
    }

    public String join(Object first, Object second, Object ... rest) {
        int count;
        StringBuilder result;

        result = new StringBuilder();
        try {
            count = appendTo(result, 0, java.util.Arrays.asList(first));
            count = appendTo(result, count, java.util.Arrays.asList(second));
            appendTo(result, count, java.util.Arrays.asList(rest));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return result.toString();
    }

    //--

    public void appendTo(StringBuilder dest, Iterable<?> lst) {
        try {
            appendTo(dest, 0, lst);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /** @return number of objects appended */
    public int appendTo(Appendable dest, int count, Iterable<?> lst) throws IOException {
        String str;

        for (Object obj : lst) {
            if (obj == null) {
                if (forNull != null) {
                    obj = forNull;
                } else if (skipNull) {
                    continue;
                } else {
                    throw new NullPointerException();
                }
            }
            str = obj.toString();
            if (trim) {
                str = str.trim();
            }
            if (skipEmpty && str.isEmpty()) {
                continue;
            }
            if (count > 0) {
                dest.append(separator);
            }
            count++;
            dest.append(str);
        }
        return count;
    }

    //-- split

    public List<String> split(String str) {
        List<String> lst;

        lst = new ArrayList<String>();
        appendTo(lst, str);
        return lst;
    }

    public void appendTo(List<String> result, String str) {
        int length;
        Matcher matcher;
        int prev;

        length = str.length();
        if (length == 0) {
            return;
        }
        matcher = pattern.matcher(str);
        prev = 0;
        while (matcher.find()) {
            add(result, str.substring(prev, matcher.start()));
            prev = matcher.end();
        }
        if (prev <= length) {
            add(result, str.substring(prev, length));
        }
    }

    private void add(List<String> result, String str) {
        if (trim) {
            str = str.trim();
        }
        if (skipEmpty && str.isEmpty()) {
            return;
        }
        result.add(str);
    }
}