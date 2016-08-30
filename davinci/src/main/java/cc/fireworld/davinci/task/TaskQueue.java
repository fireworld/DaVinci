package cc.fireworld.davinci.task;

import android.support.annotation.NonNull;

/**
 * task queue
 * Created by cxx on 16-3-8.
 * email: xx.ch@outlook.com
 */
public interface TaskQueue {

    void addTask(@NonNull Task task);

    void removeTask(@NonNull Task task);

    void finishTask(@NonNull Task task);

    interface TaskListener {

        void onSuccess(@NonNull Task task);

        void onFailure(@NonNull Task task);
    }
}
