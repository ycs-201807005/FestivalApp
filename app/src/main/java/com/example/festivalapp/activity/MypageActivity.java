package com.example.festivalapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.festivalapp.R;

public class MypageActivity extends ConfigActivity {
    private Button btnBookMarkList, btnReviewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        //북마크 목록 버튼
        btnBookMarkList = (Button)findViewById(R.id.btnBookMarkList);
        btnBookMarkList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStartActivity(BookMarkActivity.class);
            }
        });

        //리뷰 목록 버튼
        btnReviewList = (Button)findViewById(R.id.btnReviewList);
        btnReviewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStartActivity(ReviewListActivity.class);
            }
        });
    }
}