package cc.fireworld.davinci.task;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import cc.fireworld.davinci.ImageOptions;
import cc.fireworld.davinci.Loader.Loader;
import cc.fireworld.davinci.R;
import cc.fireworld.davinci.util.CryptoUtils;
import cc.fireworld.davinci.util.LogUtils;

/**
 * Task
 * Created by cxx on 15/12/10.
 * email: xx.ch@outlook.com
 */
final class Task implements Comparable<Task> {
    private static final int TAG = R.string.app_name;

    final String url;
    final String key;
    final WeakReference<ImageView> ref;
    final ImageOptions opts;
    final Loader.Listener listener;

    Task(@NonNull String url, @NonNull ImageView iv, @NonNull ImageOptions opts, Loader.Listener listener) {
        this.url = url;
        this.key = CryptoUtils.md5(url);
        this.ref = new WeakReference<>(iv);
        this.opts = opts;
        this.listener = listener;
    }

    void onPreExecute() {
        ImageView iv = ref.get();
        if (iv != null) {
            iv.setTag(TAG, key);
            if (opts.defImageId != 0) {
                iv.setImageResource(opts.defImageId);
            }
        }
    }

    void completeFromCache(@NonNull Bitmap bitmap) {
        ImageView iv = ref.get();
        if (iv != null) {
            iv.setImageBitmap(bitmap);
        }
        notifyListener(bitmap);
    }

    void completeFromServer(@NonNull Bitmap bitmap) {
        ImageView iv = ref.get();
        String tag = (iv == null ? null : iv.getTag(TAG).toString());
        LogUtils.e("TaskUrlTag", String.format("Key: %s  Tag: %s  Equals:%s", key, tag, String.valueOf(key.equals(tag))));
        if (key.equals(tag)) {
            iv.setImageBitmap(bitmap);
        }
        notifyListener(bitmap);
    }

    private void notifyListener(@NonNull Bitmap bitmap) {
        if (listener != null) {
            listener.onComplete(bitmap);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;

        Task task = (Task) o;

        if (!url.equals(task.url)) return false;
        if (!key.equals(task.key)) return false;
        if (!ref.equals(task.ref)) return false;
        if (!opts.equals(task.opts)) return false;
        return listener != null ? listener.equals(task.listener) : task.listener == null;

    }

    @Override
    public int hashCode() {
        int result = url.hashCode();
        result = 31 * result + key.hashCode();
        result = 31 * result + ref.hashCode();
        result = 31 * result + opts.hashCode();
        result = 31 * result + (listener != null ? listener.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(@NonNull Task another) {
        return key.compareTo(another.key);
    }
}
