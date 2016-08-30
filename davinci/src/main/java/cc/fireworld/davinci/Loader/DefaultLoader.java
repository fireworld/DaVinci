package cc.fireworld.davinci.Loader;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cc.fireworld.davinci.util.LogUtils;
import cc.fireworld.davinci.util.Op;

/**
 * Created by cxx on 16-3-15.
 * email: xx.ch@outlook.com
 */
public class DefaultLoader implements Loader<InputStream> {
    public static final String LOCAL_FILE_MSG = "local file ok";
    public static final String LOCAL_FILE_NOT_FOUND = "file not found";

    private Config mConfig;

    public DefaultLoader(@NonNull Config config) {
        mConfig = Op.nonNull(config, "config == null");
    }

    @NonNull
    @Override
    public Response<InputStream> load(@NonNull String url) {
        if (url.startsWith("http")) {
            return fromHttp(url);
        } else if (url.startsWith(File.separator)) {
            return fromFile(url);
        } else {
            throw new IllegalArgumentException("illegal request");
        }
    }

    private Response<InputStream> fromHttp(String url) {
        Response<InputStream> rp;
        try {
            HttpURLConnection conn = createConnection(url);
            int code = conn.getResponseCode();
            String msg = conn.getResponseMessage();
            if (code == HttpURLConnection.HTTP_OK) {
                InputStream is = conn.getInputStream();
                rp = Response.onSuccess(code, msg, is);
            } else {
                rp = Response.onFail(code, msg);
            }
        } catch (IOException e) {
            rp = Response.onFail(-1, "Can't create connection");
            LogUtils.e(e);
        }
        return rp;
    }

    private HttpURLConnection createConnection(@NonNull String url) throws IOException {
        URL u = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        conn.setConnectTimeout(mConfig.connectTimeOut);
        conn.setReadTimeout(mConfig.readTimeOut);
        conn.setRequestMethod(mConfig.requestMethod);
        return conn;
    }

    private static Response<InputStream> fromFile(String url) {
        Response<InputStream> rs;
        try {
            InputStream is = new FileInputStream(url);
            rs = Response.onSuccess(HttpURLConnection.HTTP_OK, LOCAL_FILE_MSG, is);
        } catch (FileNotFoundException e) {
            rs = Response.onFail(-1, LOCAL_FILE_NOT_FOUND);
            LogUtils.e(e);
        }
        return rs;
    }

    public static class Config {
        protected int connectTimeOut = 8000;
        protected int readTimeOut = 5000;
        protected String requestMethod = "GET";

        public Config setConnectTimeOut(int timeoutMillis) {
            if (timeoutMillis > 1000) {
                this.connectTimeOut = timeoutMillis;
            }
            return this;
        }

        public Config setReadTimeOut(int timeoutMillis) {
            if (timeoutMillis > 1000) {
                this.readTimeOut = timeoutMillis;
            }
            return this;
        }
    }
}
