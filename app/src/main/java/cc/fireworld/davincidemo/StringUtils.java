package cc.fireworld.davincidemo;

import org.json.JSONObject;


/**
 * Created by cxx on 15/12/1.
 * email: xx.ch@outlook.com
 */
public class StringUtils {

    public static String getString(JSONObject jsonObject, String key) {
        return getString(jsonObject, key, "");
    }

    public static String getString(JSONObject jsonObject, String key, String defaultValue) {
        String result = defaultValue;
        try {
            result = jsonObject.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
