package com.github.elic0de.eliccommon.util;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public final class Validate {


    @NonNull
    public static <T> T notNull(@Nullable T object) {
        if (object == null)
            throw new NullPointerException();
        return object;
    }

    @NonNull
    public static <T> T notNull(@Nullable T object, @NonNull String message) {
        if (object == null)
            throw new NullPointerException(message);
        return object;
    }

    public static void isTrue(boolean condition) {
        if (condition)
            throw new IllegalArgumentException();
    }

    public static void isTrue(boolean condition, @NonNull String message) {
        if (condition)
            throw new IllegalArgumentException(message);
    }

    public static void isFalse(boolean condition) {
        Validate.isTrue(!condition);
    }

    public static void isFalse(boolean condition, @NonNull String message) {
        Validate.isTrue(!condition, message);
    }
}