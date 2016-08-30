package cc.fireworld.davinci;

import android.content.res.Resources;
import android.support.annotation.DrawableRes;
import android.util.DisplayMetrics;

/**
 * Image options
 * Created by cxx on 16-3-8.
 * email: xx.ch@outlook.com
 */
public class ImageOptions {
    private static final int DEFAULT_WIDTH;
    private static final int DEFAULT_HEIGHT;

    public final int width;
    public final int height;
    public final int defImageId;

    static {
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        DEFAULT_WIDTH = dm.widthPixels;
        DEFAULT_HEIGHT = dm.heightPixels;
    }

    public ImageOptions(int width, int height) {
        this(width, height, 0);
    }

    public ImageOptions(int width, int height, @DrawableRes int defImageId) {
        this.width = width > 0 ? width : DEFAULT_WIDTH;
        this.height = height > 0 ? height : DEFAULT_HEIGHT;
        this.defImageId = defImageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageOptions)) return false;

        ImageOptions that = (ImageOptions) o;

        if (width != that.width) return false;
        if (height != that.height) return false;
        return defImageId == that.defImageId;

    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        result = 31 * result + defImageId;
        return result;
    }
}
