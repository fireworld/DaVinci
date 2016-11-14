package cc.fireworld.davinci.task;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.Map;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import cc.fireworld.davinci.ImageOptions;
import cc.fireworld.davinci.cache.Cacheable;

/**
 * wait task
 * Created by cxx on 16-3-8.
 * email: xx.ch@outlook.com
 */
public class WaitQueue implements TaskQueue {
    private Map<String, NavigableSet<Task>> tasks = new ConcurrentHashMap<>(); // waiting tasks
    private Cacheable<String, Bitmap, ImageOptions> cache;
    private Handler handler = new Handler(Looper.getMainLooper());

    protected WaitQueue(@NonNull Cacheable<String, Bitmap, ImageOptions> cache) {
        this.cache = cache;
    }

    @Override
    public void addTask(@NonNull Task task) {
        String key = task.key;
        NavigableSet<Task> set = tasks.get(key);
        if (set == null) {
            set = new ConcurrentSkipListSet<>();
            tasks.put(key, set);
        }
        set.add(task);
    }

    @Override
    public void removeTask(@NonNull Task task) {
        tasks.remove(task.key);
    }

    @Override
    public void finishTask(@NonNull Task task) {
        String key = task.key;
        Bitmap bitmap = cache.getFromL1(key);
        if (bitmap != null) {
            searchAndDisplay(key, bitmap);
        }
    }

    private void searchAndDisplay(@NonNull String key, @NonNull final Bitmap bitmap) {
        final NavigableSet<Task> set = tasks.get(key);
        tasks.remove(key);
        if (set != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    while (!set.isEmpty()) {
                        Task task = set.pollLast();
                        task.completeFromServer(bitmap);
                    }
                }
            });
        }
    }
}
