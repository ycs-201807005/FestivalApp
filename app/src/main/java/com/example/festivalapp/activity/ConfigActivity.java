package com.example.festivalapp.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ConfigActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* 가로모드 제한 */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    protected void onStartActivity(Class activityname) {
        Intent intent = new Intent(this, activityname);
        //intent.putExtra("contentid",contentid);
        startActivity(intent);
    }

    protected void Toastmsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
