package cc.fireworld.davinci.util;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;

/**
 * Created by cxx on 16-3-8.
 * email: xx.ch@outlook.com
 */
public class LogUtils {
    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NOTHING = 6;
    private static int level = NOTHING;

    public static void init(@NonNull Context ctx) {
        LogUtils.level = isDebug(ctx) ? VERBOSE : NOTHING;
    }

    private static boolean isDebug(@NonNull Context ctx) {
        try {
            Class c = Class.forName(ctx.getPackageName() + ".BuildConfig");
            Field debug = c.getField("DEBUG");
            debug.setAccessible(true);
            return debug.getBoolean(null);
        } catch (Exception e) {
            return false;
        }
    }

    public static void v(String tag, String msg) {
        if (VERBOSE >= level) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG >= level) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (INFO >= level) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (WARN >= level) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (ERROR >= level) {
            Log.e(tag, msg);
        }
    }

    public static void e(@NonNull Throwable e) {
        if (ERROR >= level) {
            e.printStackTrace();
        }
    }

    public static void setLevel(@Level int level) {
        LogUtils.level = level;
    }

    @Level
    public static int getLevel() {
        return level;
    }

    private LogUtils() {
        throw new AssertionError("no instance.");
    }

    @IntDef({VERBOSE, DEBUG, INFO, WARN, ERROR, NOTHING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Level {
    }
}
