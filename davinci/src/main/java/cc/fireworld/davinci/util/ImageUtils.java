package cc.fireworld.davinci.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import cc.fireworld.davinci.ImageOptions;


/**
 * Some methods to handle the conversion between bitmap and input stream.
 * Created by cxx on 15/12/8.
 * email: xx.ch@outlook.com
 */
public class ImageUtils {

    public static Bitmap decodeByteArray(@NonNull byte[] data, @NonNull ImageOptions opts) {
        ByteArrayInputStream is = new ByteArrayInputStream(data);
        Bitmap bitmap = decodeStream(is, opts);
        IOUtils.close(is);
        return bitmap;
    }

    @Nullable
    public static Bitmap decodeStream(@NonNull InputStream is, @NonNull ImageOptions opts) {
        return decodeStream(is, opts.width, opts.height);
    }

    @Nullable
    private static Bitmap decodeStream(@NonNull InputStream is, int reqWidth, int reqHeight) {
        Bitmap result = null;
        BufferedInputStream bis = new BufferedInputStream(is);
        try {
            bis.mark(bis.available());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(bis, null, options);
            if (!options.mCancel && options.outWidth != -1 && options.outHeight != -1) {
                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
                options.inJustDecodeBounds = false;
                bis.reset();
                result = BitmapFactory.decodeStream(bis, null, options);
            }
        } catch (Exception e) {
            LogUtils.e(e);
        } finally {
            IOUtils.close(bis);
        }
        return result;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private ImageUtils() {
        throw new AssertionError("No instance");
    }
}
