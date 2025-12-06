package com.shitlime.psj;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * A final utility class providing a minimalist implementation for converting Java objects to JSON strings.
 * <p>
 * This class is designed to handle common data types including primitives, Strings, Maps, Lists, and arrays.
 * It also includes basic protection against circular references during serialization.
 * <p>
 * This class is not intended to be a fully compliant JSON library but serves the specific needs of the project.
 */
public final class Utils {
    private Utils() {}

    private static final Map<Character, String> ESCAPE_CHARS = Map.ofEntries(
            Map.entry('"', "\\\""),
            Map.entry('\\', "\\\\"),
            Map.entry('\b', "\\b"),
            Map.entry('\f', "\\f"),
            Map.entry('\n', "\\n"),
            Map.entry('\r', "\\r"),
            Map.entry('\t', "\\t"),
            Map.entry('/', "\\/")
    );

    /**
     * Converts a given Java object into its JSON string representation.
     * This is the main entry point for the JSON conversion.
     *
     * @param obj The object to be converted to a JSON string.
     * @return A string containing the JSON representation of the object.
     */
    protected static String toJSONString(Object obj) {
        StringBuilder sb = new StringBuilder(1024);
        appendValue(sb, obj, new IdentityHashMap<>());
        return sb.toString();
    }

    private static void appendValue(StringBuilder sb, Object obj, IdentityHashMap<Object, Object> visited) {
        if (obj == null) {
            sb.append("null");
        } else if (obj instanceof String) {
            appendString(sb, (String) obj);
        } else if (obj instanceof Map) {
            appendMap(sb, (Map<?, ?>) obj, visited);
        } else if (obj instanceof List) {
            appendList(sb, (List<?>) obj, visited);
        } else if (obj instanceof Number) {
            appendNumber(sb, (Number) obj);
        } else if (obj instanceof Boolean) {
            sb.append(obj);
        } else if (obj.getClass().isArray()) {
            appendArray(sb, obj, visited);
        } else {
            appendString(sb, obj.toString());
        }
    }

    private static void appendString(StringBuilder sb, String str) {
        sb.append('"');
        for (int i = 0; i < str.length(); i++) {
            appendEscapedChar(sb, str.charAt(i));
        }
        sb.append('"');
    }

    private static void appendEscapedChar(StringBuilder sb, char c) {
        String escaped = ESCAPE_CHARS.get(c);
        if (escaped != null) {
            sb.append(escaped);
        } else if (c >= ' ' && c <= '~') {
            sb.append(c);
        } else {
            sb.append(String.format("\\u%04X", (int) c));
        }
    }

    private static void appendMap(StringBuilder sb, Map<?, ?> map, IdentityHashMap<Object, Object> visited) {
        if (visited.containsKey(map)) {
            sb.append("{\"$ref\":\"circular\"}");
            return;
        }
        visited.put(map, null);

        sb.append('{');
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) {
                sb.append(',');
            }
            appendString(sb, String.valueOf(entry.getKey()));
            sb.append(':');
            appendValue(sb, entry.getValue(), visited);
            first = false;
        }
        sb.append('}');
    }

    private static void appendList(StringBuilder sb, List<?> list, IdentityHashMap<Object, Object> visited) {
        if (visited.containsKey(list)) {
            sb.append("[\"$ref\":\"circular\"]");
            return;
        }
        visited.put(list, null);

        sb.append('[');
        boolean first = true;
        for (Object item : list) {
            if (!first) {
                sb.append(',');
            }
            appendValue(sb, item, visited);
            first = false;
        }
        sb.append(']');
    }

    private static void appendNumber(StringBuilder sb, Number num) {
        // NaN | Infinity
        if (num instanceof Double || num instanceof Float) {
            double d = num.doubleValue();
            if (Double.isNaN(d) || Double.isInfinite(d)) {
                sb.append("null");
                return;
            }
        }
        sb.append(num);
    }

    private static void appendArray(StringBuilder sb, Object array, IdentityHashMap<Object, Object> visited) {
        if (visited.containsKey(array)) {
            sb.append("[\"$ref\":\"circular\"]");
            return;
        }
        visited.put(array, null);

        sb.append('[');
        int length = java.lang.reflect.Array.getLength(array);
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            appendValue(sb, java.lang.reflect.Array.get(array, i), visited);
        }
        sb.append(']');
    }
}

