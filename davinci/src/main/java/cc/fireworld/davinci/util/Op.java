package cc.fireworld.davinci.util;

/**
 * Created by cxx on 16-8-11.
 * xx.ch@outlook.com
 */
public final class Op {
    public static <T> T orElse(T value, T other) {
        return value != null ? value : other;
    }

    public static <T> T nonNull(T value) {
        return nonNull(value, "value == null");
    }

    public static <T> T nonNull(T value, String msg) {
        if (value == null) {
            throw new NullPointerException(msg);
        }
        return value;
    }

    public static <T, E extends Throwable> T nullThrow(T value, E e) throws E {
        if (value == null) {
            throw e;
        }
        return value;
    }

    private Op() {
        throw new AssertionError("no instance");
    }
}
