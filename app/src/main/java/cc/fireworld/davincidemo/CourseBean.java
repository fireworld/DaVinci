package cc.fireworld.davincidemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Course info.
 * Created by cxx on 15/12/1.
 * email: xx.ch@outlook.com
 */
public class CourseBean {
    private String name;
    private String picSmallUrl;
    private String picBigUrl;
    private String description;

    public CourseBean() {
    }

    public CourseBean(String name, String picSmallUrl, String description) {
        this.name = name;
        this.picSmallUrl = picSmallUrl;
        this.description = description;
    }

    public CourseBean(String name, String picSmallUrl, String picBigUrl, String description) {
        this.name = name;
        this.picSmallUrl = picSmallUrl;
        this.picBigUrl = picBigUrl;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicSmallUrl() {
        return picSmallUrl;
    }

    public void setPicSmallUrl(String picSmallUrl) {
        this.picSmallUrl = picSmallUrl;
    }

    public String getPicBigUrl() {
        return picBigUrl;
    }

    public void setPicBigUrl(String picBigUrl) {
        this.picBigUrl = picBigUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static CourseBean fromJSONObject(JSONObject jsonObject) {
        String name = StringUtils.getString(jsonObject, "name");
        String picSmallUrl = StringUtils.getString(jsonObject, "picSmall");
        String picBigUrl = StringUtils.getString(jsonObject, "picBig");
        String description = StringUtils.getString(jsonObject, "description");
        return new CourseBean(name, picSmallUrl, picBigUrl, description);
    }

    public static List<CourseBean> fromJSONArray(JSONArray array) {
        List<CourseBean> list = new ArrayList<>(array.length());
        for (int i = 0; i < array.length(); i++) {
            try {
                list.add(fromJSONObject(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
