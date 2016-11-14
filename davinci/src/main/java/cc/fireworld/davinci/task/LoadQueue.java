package cc.fireworld.davinci.task;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cc.fireworld.davinci.ImageOptions;
import cc.fireworld.davinci.Loader.Loader;
import cc.fireworld.davinci.Loader.Response;
import cc.fireworld.davinci.cache.Cacheable;
import cc.fireworld.davinci.util.IOUtils;
import cc.fireworld.davinci.util.LogUtils;

/**
 * download task
 * Created by cxx on 16-3-8.
 * email: xx.ch@outlook.com
 */
public class LoadQueue implements TaskQueue {
    private ExecutorService executor = new ThreadPoolExecutor(6, 10, 60L, TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>(), new ThreadPoolExecutor.DiscardOldestPolicy());

    private Set<String> tasks = new ConcurrentSkipListSet<>(); // running task.
    private Cacheable<String, Bitmap, ImageOptions> cache;
    private Loader<InputStream> loader;
    private TaskListener listener;

    public LoadQueue(Cacheable<String, Bitmap, ImageOptions> cache, Loader<InputStream> loader, TaskListener listener) {
        this.cache = cache;
        this.loader = loader;
        this.listener = listener;
    }

    @Override
    public void addTask(@NonNull Task task) {
        task.onPreExecute();
        if (tasks.add(task.key)) {
            executeTask(task);
        }
    }

    private void executeTask(final Task task) {
        LogUtils.i("ExecuteTask", "Url: " + task.url);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (fromCache(task)) return;
                Response<InputStream> rs = loader.load(task.url);
                if (rs.code == HttpURLConnection.HTTP_OK && rs.data != null) {
                    cache.saveToL2(task.key, rs.data);
                    IOUtils.close(rs.data);
                    if (fromCache(task)) return;
                }
                listener.onFailure(task);
            }
        });
    }

    private boolean fromCache(Task task) {
        Bitmap bitmap = cache.get(task.key, task.opts);
        if (bitmap != null) {
            listener.onSuccess(task);
            return true;
        }
        return false;
    }

    @Override
    public void finishTask(@NonNull Task task) {
        tasks.remove(task.key);
    }

    @Override
    public void removeTask(@NonNull Task task) {
        tasks.remove(task.key);
    }
}
