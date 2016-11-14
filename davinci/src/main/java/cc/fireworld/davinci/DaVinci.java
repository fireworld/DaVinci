package cc.fireworld.davinci;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import java.io.InputStream;

import cc.fireworld.davinci.Loader.DefaultLoader;
import cc.fireworld.davinci.Loader.Loader;
import cc.fireworld.davinci.cache.BitmapCache;
import cc.fireworld.davinci.cache.Cacheable;
import cc.fireworld.davinci.task.TaskCenter;
import cc.fireworld.davinci.util.LogUtils;
import cc.fireworld.davinci.util.OpUtils;

/**
 * Simple Demo:
 * DaVinci.getInstance().init(getApplicationContext());
 * DaVinci.getInstance().display(url, imageView);
 * <p/>
 * Created by cxx on 16-3-8.
 * email: xx.ch@outlook.com
 */
public class DaVinci {
    private BitmapCache.Config cacheConfig;
    private DefaultLoader.Config loaderConfig;
    private ImageOptions defOpts;

    private TaskCenter taskCenter;
    private Loader<InputStream> loader;
    private Cacheable<String, Bitmap, ImageOptions> cache;

    private DaVinci() {
    }

    public static DaVinci getInstance() {
        return Holder.instance;
    }

    /**
     * 初始化之前调用才有效
     *
     * @param config BitmapCache.Config
     * @return DaVinci
     */
    public DaVinci configCache(BitmapCache.Config config) {
        cacheConfig = config;
        return this;
    }

    /**
     * 初始化之前调用才有效
     *
     * @param config DefaultLoader.Config
     * @return DaVinci
     */
    public DaVinci configLoader(DefaultLoader.Config config) {
        loaderConfig = config;
        return this;
    }

    /**
     * 初始化之前调用才有效
     *
     * @param opts ImageOptions
     * @return DaVinci
     */
    public DaVinci setDefaultImageOptions(ImageOptions opts) {
        defOpts = opts;
        return this;
    }

    /**
     * 设置默认的下载器，初始化之前调用才有效。
     *
     * @param loader Loader
     * @return DaVinci
     */
    public DaVinci setDefaultLoader(@NonNull Loader<InputStream> loader) {
        this.loader = OpUtils.nonNull(loader, "loader == null");
        return this;
    }

    /**
     * 设置默认的缓存器，初始化之前调用才有效。
     *
     * @param cache Cacheable<String, Bitmap, ImageOptions>
     * @return DaVinci
     */
    public DaVinci setDefaultCache(Cacheable<String, Bitmap, ImageOptions> cache) {
        this.cache = OpUtils.nonNull(cache, "cache == null");
        return this;
    }

    public void init(@NonNull Context context) {
        if (taskCenter != null) return;
        initCache(OpUtils.nonNull(context, "context ==  null"));
        initLoader();
        initDefOpts();
        taskCenter = new TaskCenter(cache, loader, defOpts);
        LogUtils.init(context);
    }

    private void initCache(Context context) {
        if (cache == null) {
            if (cacheConfig == null) {
                cacheConfig = new BitmapCache.Config(context);
            }
            cache = new BitmapCache(context, cacheConfig);
        }
    }

    private void initLoader() {
        if (loader == null) {
            if (loaderConfig == null) {
                loaderConfig = new DefaultLoader.Config();
            }
            loader = new DefaultLoader(loaderConfig);
        }
    }

    private void initDefOpts() {
        if (defOpts == null) {
            defOpts = new ImageOptions(-1, -1);
        }
    }

    public void display(@NonNull String url, ImageView view) {
        this.display(url, view, null, null);
    }

    public void display(@NonNull String url, ImageView view, ImageOptions opts) {
        this.display(url, view, opts, null);
    }

    public void display(@NonNull String url, ImageView view, Loader.Listener listener) {
        this.display(url, view, null, listener);
    }

    public void display(@NonNull String url, ImageView view, ImageOptions opts, Loader.Listener listener) {
        if (taskCenter == null) {
            throw new IllegalStateException("uninitialized");
        }
        taskCenter.display(url, view, opts, listener);
    }

    private static class Holder {
        private static DaVinci instance = new DaVinci();
    }
}
