package cc.fireworld.davinci.task;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cc.fireworld.davinci.ImageOptions;
import cc.fireworld.davinci.Loader.Loader;
import cc.fireworld.davinci.cache.Cacheable;
import cc.fireworld.davinci.util.OpUtils;

/**
 * Image Center, dispatch task.
 * Created by cxx on 16-3-8.
 * email: xx.ch@outlook.com
 */
public class TaskCenter implements TaskQueue.TaskListener {
    private static final int TRY_NUM = 3;

    private ImageOptions defOpts;
    private TaskQueue loadQueue;
    private TaskQueue waitQueue;
    private Cacheable<String, Bitmap, ImageOptions> cache;
    private Map<String, Integer> failTask = new ConcurrentHashMap<>();

    public TaskCenter(@NonNull Cacheable<String, Bitmap, ImageOptions> cache, @NonNull Loader<InputStream> loader, @NonNull ImageOptions defOpts) {
        this.cache = cache;
        this.loadQueue = new LoadQueue(this.cache, loader, this);
        this.waitQueue = new WaitQueue(this.cache);
        this.defOpts = defOpts;
    }

    public void display(@NonNull String url, @NonNull ImageView iv, @Nullable ImageOptions opts, @Nullable Loader.Listener listener) {
        Task task = createTask(url, iv, opts, listener);
        if (!searchAndDisplayInL1(task)) {
            addTask(task);
        }
    }

    private Task createTask(String url, ImageView iv, ImageOptions opts, Loader.Listener listener) {
        String u = OpUtils.nonNull(url, "url == null");
        ImageView v = OpUtils.nonNull(iv, "iv == null");
        ImageOptions o = OpUtils.orElse(opts, defOpts);
        return new Task(u, v, o, listener);
    }

    private boolean searchAndDisplayInL1(Task task) {
        Bitmap bitmap = cache.getFromL1(task.key);
        if (bitmap != null) {
            task.completeFromCache(bitmap);
            return true;
        }
        return false;
    }

    private void addTask(Task task) {
        loadQueue.addTask(task);
        waitQueue.addTask(task);
    }

    @Override
    public void onSuccess(@NonNull Task task) {
        waitQueue.finishTask(task);
        loadQueue.finishTask(task);
        failTask.remove(task.key);
    }

    @Override
    public void onFailure(@NonNull Task task) {
        String key = task.key;
        Integer value = failTask.get(key);
        Integer num = (value == null ? 1 : value);
        if (num > TRY_NUM) {
            failTask.remove(key);
            removeTask(task);
        } else {
            failTask.put(key, ++num);
            retryTask(task);
        }
    }

    private void removeTask(Task task) {
        loadQueue.removeTask(task);
        waitQueue.removeTask(task);
    }

    private void retryTask(Task task) {
        loadQueue.removeTask(task);
        loadQueue.addTask(task);
    }
}

