package cc.fireworld.davincidemo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cc.fireworld.davinci.DaVinci;
import cc.fireworld.davinci.ImageOptions;
import cc.fireworld.davinci.util.LogUtils;


public class MainActivity extends Activity {
    private static final String HOST = "http://www.imooc.com/api/teacher?type=4&num=30";

    private List<CourseBean> mList = new ArrayList<>();
    private BaseAdapter mAdapter;
    private AsyncTask<String, Void, List<CourseBean>> mAsyncTask = new AsyncTask<String, Void, List<CourseBean>>() {

        @Override
        protected List<CourseBean> doInBackground(String... params) {
            List<CourseBean> list = null;
            try {
                String jsonString = IOUtils.readStream(HOST, "utf-8");
                list = getCourseBeanFromJSON(jsonString);
                saveStringToSP("json", jsonString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<CourseBean> courseBeans) {
            super.onPostExecute(courseBeans);
            if (courseBeans != null) {
                mList.addAll(courseBeans);
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initDaVinci();
        initData();
    }

    private void initDaVinci() {
        DaVinci.getInstance().setDefaultImageOptions(new ImageOptions(500, 500, R.mipmap.ic_launcher)).init(getApplicationContext());
//        DaVinci.getInstance().init(getApplicationContext());
    }


    private void initView() {
        ListView listView = (ListView) findViewById(R.id.lv_main);
        mAdapter = new CommonBaseAdapter<CourseBean>(this, mList, R.layout.adapter_layout) {
            @Override
            public void convert(ViewHolder holder, CourseBean courseBean) {
                holder.setText(R.id.tv_name, courseBean.getName())
                        .setText(R.id.tv_description, courseBean.getDescription());
                ImageView imageView = holder.getView(R.id.iv_icon);
                DaVinci.getInstance().display(courseBean.getPicSmallUrl(), imageView);
                LogUtils.e("MainActivity", holder.getPosition() + " : " + courseBean.getPicBigUrl());
//                Picasso.with(MainActivity.this).load(courseBean.getPicBigUrl()).into(imageView);
            }
        };
        listView.setAdapter(mAdapter);
    }

    private void initData() {
        if (netIsAvailable()) {
            mAsyncTask.execute(HOST);
        } else {
            String jsonString = getStringFromSP("json");
            if (!TextUtils.isEmpty(jsonString)) {
                List<CourseBean> list = getCourseBeanFromJSON(jsonString);
                if (list != null) {
                    mList.addAll(list);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private boolean netIsAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isAvailable();
    }

    private String getStringFromSP(String key) {
        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
        return sp.getString(key, "");
    }

    private List<CourseBean> getCourseBeanFromJSON(String jsonString) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonString);
            JSONArray array = jsonObject.getJSONArray("data");
            return CourseBean.fromJSONArray(array);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveStringToSP(String key, String content) {
        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, content);
        editor.apply();
    }
}
