package org.example.api.gw.utils;

public class Checks {

    public static String assertNotEmpty(String s, String message) {
        var result = Strings.nullSanitized(s);
        if (result.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return result;
    }

    public static <E> E assertNotNull(E o, String message) {
        if (o == null) {
            throw new IllegalArgumentException(message);
        }
        return o;
    }

    public static void assertCondition(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static String actualString(Object value) {
        return String.format("Actual value: '%s'", value);
    }
}
