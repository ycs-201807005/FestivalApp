package com.example.festivalapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.festivalapp.FirebaseFuncs;
import com.example.festivalapp.R;

public class HomeActivity extends ConfigActivity {
    private Button btnLogout,btnMypage,btnBookMark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //로그아웃 버튼
        btnLogout = (Button)findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStartActivity(LoginActivity.class);
            }
        });

        //마이페이지 버튼
        btnMypage = (Button)findViewById(R.id.btnMypage);
        btnMypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStartActivity(MypageActivity.class);
            }
        });

        //북마크 버튼
        btnBookMark = (Button)findViewById(R.id.btnBookMark);
        btnBookMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStartActivity(BookMarkActivity.class);
            }
        });
    }

}