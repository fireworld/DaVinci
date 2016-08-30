package cc.fireworld.davinci.Loader;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;


/**
 * load http or local file
 * Created by cxx on 16-3-8.
 * email: xx.ch@outlook.com
 */
public interface Loader<T> {
    @NonNull
    Response<T> load(@NonNull String url);

    interface Listener {
        void onComplete(@NonNull Bitmap bitmap);
    }
}
