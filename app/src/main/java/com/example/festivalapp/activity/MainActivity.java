package com.example.festivalapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.festivalapp.R;
import com.example.festivalapp.activity.ConfigActivity;
import com.example.festivalapp.activity.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class MainActivity extends ConfigActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.logoutButton).setOnClickListener(onClickListener);
        findViewById(R.id.profileupdateButton).setOnClickListener(onClickListener);
        findViewById(R.id.profileButton).setOnClickListener(onClickListener);
        findViewById(R.id.writepostButton).setOnClickListener(onClickListener);
        findViewById(R.id.viewpostButton).setOnClickListener(onClickListener);
        findViewById(R.id.bookmarktestButton).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.logoutButton:
                    /* 로그아웃 */
                    FirebaseAuth.getInstance().signOut();
                    //로그인 화면으로 이동
                    onStartActivity(LoginActivity.class);
                    break;
                case R.id.profileupdateButton:
                    //myStartActivity(MemberinitActivity.class);
                    break;
                case R.id.profileButton:
                    //myStartActivity(ShowinfoActivity.class);
                    break;
                case R.id.writepostButton:
                    //myStartActivity(WritePostActivity.class);
                    break;
                case R.id.viewpostButton:
                    //myStartActivity(ViewPostActivity.class);
                    break;
                case R.id.bookmarktestButton:
                    //myStartActivity(BookmarkActivity.class);
                    break;
            }
        }
    };

}