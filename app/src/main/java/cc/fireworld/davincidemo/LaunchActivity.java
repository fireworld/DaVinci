package cc.fireworld.davincidemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by cxx on 15/12/13.
 * email: xx.ch@outlook.com
 */
public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
    }

    public void toMain(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }
}
