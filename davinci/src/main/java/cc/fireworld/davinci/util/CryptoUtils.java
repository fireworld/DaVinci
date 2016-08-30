package cc.fireworld.davinci.util;

import android.support.annotation.NonNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 encrypted
 * Created by cxx on 15/12/8.
 * email: xx.ch@outlook.com
 */
public class CryptoUtils {

    public static String md5(@NonNull String source) {
        String result;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(source.getBytes());
            result = toHex(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            result = String.valueOf(source.hashCode());
        }
        return result;
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    private CryptoUtils() {
        throw new AssertionError("No instance");
    }
}
