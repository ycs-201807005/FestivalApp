package com.example.festivalapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.festivalapp.FirebaseFuncs;
import com.example.festivalapp.R;

public class ReviewContentActivity extends ConfigActivity {
    private Button btnInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_content);

        //파이어베이스 - 해당 리뷰 정보 가져오기
        FirebaseFuncs firebaseFuncs = new FirebaseFuncs();
        firebaseFuncs.GetReview();

        //축제정보페이지로 이동
        btnInfo=(Button)findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onStartActivity(InfopageActivity.class);
            }
        });
    }
}