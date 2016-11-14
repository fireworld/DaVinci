package cc.fireworld.davinci.Loader;

import android.support.annotation.NonNull;

import cc.fireworld.davinci.util.OpUtils;

/**
 * loader response
 * Created by cxx on 16-3-9.
 * email: xx.ch@outlook.com
 */
public class Response<T> {
    public final int code;
    public final String msg;
    public final T data;

    private Response(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Response<T> onSuccess(int code, String msg, @NonNull T data) {
        return new Response<>(code, msg, OpUtils.nonNull(data, "data == null"));
    }

    public static <T> Response<T> onFail(int code, String msg) {
        return new Response<>(code, msg, null);
    }
}