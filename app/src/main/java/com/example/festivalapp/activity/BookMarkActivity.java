package com.example.festivalapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.festivalapp.FirebaseFuncs;
import com.example.festivalapp.R;

public class BookMarkActivity extends ConfigActivity {
    private Button btnInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_mark);

        //파이어베이스 - 북마크 리스트 가져오기
        FirebaseFuncs firebaseFuncs = new FirebaseFuncs();
        firebaseFuncs.GetBookMarkList();

        //축제정보페이지로 이동
        btnInfo=(Button)findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStartActivity(InfopageActivity.class);
            }
        });

    }
}