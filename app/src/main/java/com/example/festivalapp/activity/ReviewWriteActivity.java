package com.example.festivalapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.festivalapp.FirebaseFuncs;
import com.example.festivalapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReviewWriteActivity extends ConfigActivity {
    private static final String TAG = "ReviewWriteActivity";

    private Button btnRvInsert;

    private String contentid,title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);

        Intent intent = getIntent();
        contentid = intent.getStringExtra("contentid");
        title = intent.getStringExtra("title");
        TextView tvTitle = (TextView)findViewById(R.id.tvTitle);
        tvTitle.setText(title);

        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
        String current = sdformat.format(new Date());
        TextView tvToday = (TextView)findViewById(R.id.tvToday);
        tvToday.setText("date : " + current);

        //리뷰 등록 버튼
        btnRvInsert = (Button)findViewById(R.id.btnRvInsert);
        btnRvInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //별점
                RatingBar ratingBar = (RatingBar)findViewById(R.id.ratingBar);
                float rating = ratingBar.getRating();
                Log.e(TAG,"rating=" + rating);

                //Firebase - 리뷰 등록

                /* @Review List Activity 업데이트 */

                finish();
            }
        });

    }
}