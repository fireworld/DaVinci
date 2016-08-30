package cc.fireworld.davinci.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.LruCache;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cc.fireworld.davinci.ImageOptions;
import cc.fireworld.davinci.util.DroidUtils;
import cc.fireworld.davinci.util.IOUtils;
import cc.fireworld.davinci.util.ImageUtils;
import cc.fireworld.davinci.util.LogUtils;

/**
 * Bitmap cache.
 * Created by cxx on 16-3-8.
 * email: xx.ch@outlook.com
 */
public class BitmapCache implements Cacheable<String, Bitmap, ImageOptions> {
    private static final int VALUE_COUNT = 1;

    private Config config;
    private LruCache<String, Bitmap> l1Cache; // memory cache
    private DiskLruCache l2Cache; // disk cache

    public BitmapCache(@NonNull Context ctx, @NonNull Config config) {
        this.config = config;
        initL1Cache();
        initL2Cache(ctx);
    }

    private void initL1Cache() {
        long maxMemory = Runtime.getRuntime().maxMemory();
        int cacheSize = (int) (maxMemory * config.ratioL1Cache);
        l1Cache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    private void initL2Cache(@NonNull Context ctx) {
        try {
            int versionCode = DroidUtils.getVersionCode(ctx);
            l2Cache = DiskLruCache.open(config.cacheDir, versionCode, VALUE_COUNT, config.sizeL2Cache);
        } catch (IOException e) {
            LogUtils.e(e);
        }
    }

    @Nullable
    @Override
    public Bitmap get(@NonNull String key, @NonNull ImageOptions opts) {
        Bitmap b = getFromL1(key);
        if (b == null) {
            b = getFromL2(key, opts);
            if (b != null) {
                saveToL1(key, b);
            }
        }
        return b;
    }

    @Nullable
    @Override
    public Bitmap getFromL1(@NonNull String key) {
        return l1Cache.get(key);
    }

    @Nullable
    @Override
    public Bitmap getFromL2(@NonNull String key, @NonNull ImageOptions opts) {
        Bitmap result = null;
        DiskLruCache.Snapshot snapshot = null;
        InputStream is = null;
        try {
            snapshot = l2Cache.get(key);
            if (snapshot != null) {
                is = snapshot.getInputStream(0);
                if (is != null) {
                    result = ImageUtils.decodeStream(is, opts);
                }
            }
        } catch (Exception e) {
            LogUtils.e(e);
        } finally {
            IOUtils.close(snapshot, is);
        }
        return result;
    }

    @Override
    public void save(@NonNull String key, @NonNull Bitmap bitmap, @NonNull ImageOptions opts) {
        saveToL1(key, bitmap);
        saveToL2(key, bitmap, opts);
    }

    @Override
    public void saveToL1(@NonNull String key, @NonNull Bitmap bitmap) {
        if (getFromL1(key) == null) {
            l1Cache.put(key, bitmap);
        }
    }

    @Override
    public void saveToL2(@NonNull String key, @NonNull Bitmap bitmap, @NonNull ImageOptions opts) {
        if (!l2HasKey(key)) {
            OutputStream os = null;
            try {
                DiskLruCache.Editor editor = l2Cache.edit(key);
                if (editor != null) {
                    os = editor.newOutputStream(0);
                    if (os != null && bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)) {
                        editor.commit();
                    } else {
                        editor.abort();
                    }
                }
            } catch (Exception e) {
                LogUtils.e(e);
            } finally {
                IOUtils.close(os);
            }
        }
    }

    @Override
    public void saveToL2(@NonNull String key, @NonNull byte[] data, @NonNull ImageOptions opts) {
        if (!l2HasKey(key)) {
            OutputStream os = null;
            try {
                DiskLruCache.Editor editor = l2Cache.edit(key);
                if (editor != null) {
                    os = editor.newOutputStream(0);
                    if (os != null && IOUtils.dump(data, os)) {
                        editor.commit();
                    } else {
                        editor.abort();
                    }
                }
            } catch (Exception e) {
                LogUtils.e(e);
            } finally {
                IOUtils.close(os);
            }
        }
    }

    @Override
    public void saveToL2(@NonNull String key, @NonNull InputStream data) {
        if (!l2HasKey(key)) {
            OutputStream os = null;
            try {
                DiskLruCache.Editor editor = l2Cache.edit(key);
                if (editor != null) {
                    os = editor.newOutputStream(0);
                    if (os != null && IOUtils.dump(data, os)) {
                        editor.commit();
                    } else {
                        editor.abort();
                    }
                }
            } catch (Exception e) {
                LogUtils.e(e);
            } finally {
                IOUtils.close(os);
            }
        }
    }

    private boolean l2HasKey(String key) {
        boolean result = false;
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = l2Cache.get(key);
            result = (snapshot != null);
        } catch (IOException e) {
            LogUtils.e(e);
        } finally {
            IOUtils.close(snapshot);
        }
        return result;
    }

    public static class Config {
        private static final long MIN_SIZE = 1048576L;

        protected float ratioL1Cache = 0.15F;
        protected long sizeL2Cache = 52428800L;
        protected File cacheDir;

        public Config(@NonNull Context context) {
            cacheDir = DroidUtils.getDiskCacheDir(context);
        }

        /**
         * 设置磁盘缓存占用的最大空间, 如设置占用 50M, 则数值为 50 * 1024 * 1024
         * 此数值仅在大于或等于 1M (即 1 * 1024 * 1024) 时才有效
         *
         * @param size 磁盘缓存的空间, 以 byte 为单位.
         */
        public Config setSizeL2Cache(long size) {
            if (size >= MIN_SIZE) {
                this.sizeL2Cache = size;
            }
            return this;
        }

        /**
         * 设置可用内存缓存限制(百分比), 如默认为 0.25, 即图片缓存最大占用可用内存的 1/4
         *
         * @param ratio 仅在 {0, 1} 时有效
         */
        public Config setRatioL1Cache(float ratio) {
            if (ratio > 0F && ratio < 1F) {
                this.ratioL1Cache = ratio;
            }
            return this;
        }

        public Config setCacheDirPath(File dir) {
            if (dir != null && dir.exists()) {
                this.cacheDir = dir;
            }
            return this;
        }
    }
}
