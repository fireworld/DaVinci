package cc.fireworld.davinci.cache;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.InputStream;

/**
 * data cache
 * Created by cxx on 16-3-8.
 * email: xx.ch@outlook.com
 */
public interface Cacheable<Key, Data, Options> {
    @Nullable
    Data get(@NonNull Key key, @NonNull Options opts);

    @Nullable
    Data getFromL1(@NonNull Key key);

    @Nullable
    Data getFromL2(@NonNull Key key, @NonNull Options opts);

    void save(@NonNull Key key, @NonNull Data data, @NonNull Options opts);

    void saveToL1(@NonNull Key key, @NonNull Data data);

    void saveToL2(@NonNull Key key, @NonNull Data data, @NonNull Options opts);

    void saveToL2(@NonNull Key key, @NonNull byte[] data, @NonNull Options opts);

    void saveToL2(@NonNull Key key, @NonNull InputStream data);
}
