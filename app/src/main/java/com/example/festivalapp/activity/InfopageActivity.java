package com.example.festivalapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.festivalapp.R;
import com.example.festivalapp.activity.ConfigActivity;

public class InfopageActivity extends ConfigActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infopage);
    }
}