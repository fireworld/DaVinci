package cc.fireworld.davinci.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 * Created by cxx on 16-3-8.
 * email: xx.ch@outlook.com
 */
public class IOUtils {

    public static void close(Closeable c1, Closeable c2, Closeable c3, Closeable... cs) {
        close(c1, c2, c3);
        for (Closeable c : cs) {
            close(c);
        }
    }

    public static void close(Closeable c1, Closeable c2, Closeable c3) {
        close(c1);
        close(c2);
        close(c3);
    }

    public static void close(Closeable c1, Closeable c2) {
        close(c1);
        close(c2);
    }

    public static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException ignore) {
            }
        }
    }

    public static void disconnect(HttpURLConnection conn) {
        if (conn != null) {
            conn.disconnect();
        }
    }

    public static boolean dump(@NonNull byte[] data, @NonNull OutputStream os) {
        ByteArrayInputStream is = new ByteArrayInputStream(data);
        boolean result = dump(is, os);
        close(is);
        return result;
    }

    @Nullable
    public static byte[] getByteArray(@NonNull InputStream is) {
        byte[] result = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (dump(is, bos)) {
            result = bos.toByteArray();
            close(bos);
        }
        return result;
    }

    public static boolean dump(@NonNull InputStream is, @NonNull OutputStream os) {
        BufferedInputStream bis = new BufferedInputStream(is);
        BufferedOutputStream bos = new BufferedOutputStream(os);
        try {
            byte[] buffer = new byte[1024];
            for (int length = bis.read(buffer); length > 0; length = bis.read(buffer)) {
                bos.write(buffer, 0, length);
            }
            bos.flush();
            return true;
        } catch (IOException e) {
            LogUtils.e(e);
            return false;
        } finally {
            close(bis, bos);
        }
    }

    private IOUtils() {
        throw new AssertionError("No instance");
    }
}
