package com.enthusiasm.plurelogger.utils;

public record Negatable<T>(T property, boolean allowed) {
    public static <U> Negatable<U> allow(U value) {
        return new Negatable<>(value, true);
    }

    public static <U> Negatable<U> deny(U value) {
        return new Negatable<>(value, false);
    }
}
