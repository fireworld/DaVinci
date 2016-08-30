package cc.fireworld.davinci.util;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

import java.io.File;

/**
 * Some methods for getting screen resolution, cache paths and so on.
 * Created by cxx on 15/12/8.
 * email: xx.ch@outlook.com
 */
public class DroidUtils {

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    /**
     * @return The absolute height of the display in pixels.
     */
    public static int getScreenHeight(@NonNull Context context) {
        return getDisplayMetrics(context).heightPixels;
    }

    /**
     * @return The absolute width of the display in pixels.
     */
    public static int getScreenWidth(@NonNull Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    private static DisplayMetrics getDisplayMetrics(@NonNull Context context) {
        return context.getResources().getDisplayMetrics();
    }

    /**
     * @return The path of the directory holding application cache files on external storage.
     * If null, it returns the path of the directory holding application cache files.
     */
    public static File getDiskCacheDir(@NonNull Context ctx) {
        File cachePath = ctx.getCacheDir();
        if (cachePath == null) {
            cachePath = ctx.getExternalCacheDir();
        }
        return cachePath;
    }

    /**
     * @return The version number of this package, or 1 if it fails.
     */
    public static int getVersionCode(@NonNull Context ctx) {
        try {
            return ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            LogUtils.e(e);
            return 1;
        }
    }

    private DroidUtils() {
        throw new AssertionError("No instance");
    }
}
