package com.example.festivalapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.festivalapp.FirebaseFuncs;
import com.example.festivalapp.R;

public class ReviewWriteActivity extends AppCompatActivity {
    private Button btnRvInsert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);

        //리뷰 등록 버튼
        btnRvInsert = (Button)findViewById(R.id.btnRvInsert);
        btnRvInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Firebase - 리뷰 등록
                FirebaseFuncs firebaseFuncs = new FirebaseFuncs();
                firebaseFuncs.InserReview();

                /* @Review List Activity 업데이트 되어야함! */


                finish();
            }
        });

    }
}