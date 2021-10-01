package com.example.festivalapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.festivalapp.FirebaseFuncs;
import com.example.festivalapp.R;

public class ReviewListActivity extends ConfigActivity {
    private Button btnRvWrite, btnRvSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);

        //파이어베이스 - 회원 리뷰 목록 가져오기
        FirebaseFuncs firebaseFuncs = new FirebaseFuncs();
        firebaseFuncs.GetReviewList_My();

        //리뷰 작성 버튼
        btnRvWrite = (Button)findViewById(R.id.btnRvWrite);
        btnRvWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStartActivity(ReviewWriteActivity.class);
            }
        });

        //리뷰 보기 버튼
        btnRvSelect = (Button)findViewById(R.id.btnRvSelect);
        btnRvSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStartActivity(ReviewContentActivity.class);
            }
        });

    }
}