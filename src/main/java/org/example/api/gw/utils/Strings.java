package org.example.api.gw.utils;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class Strings {
    public static boolean isAbsent(String s) {
        return nullToEmpty(s).trim().isEmpty();
    }

    public static String nullToEmpty(String s) {
        return Objects.toString(s, "");
    }

    public static String nullSanitized(@Nullable String s) {
        return Objects.toString(s, "").trim();
    }
}
