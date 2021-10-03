package com.example.festivalapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.festivalapp.FirebaseFuncs;
import com.example.festivalapp.R;

public class JoinActivity extends ConfigActivity {
    private Button btnOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        /*
        btnOK = (Button)findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Firebase - 회원 등록
                FirebaseFuncs firebaseFuncs = new FirebaseFuncs();
                firebaseFuncs.SignUp();

                finish();
            }
        });
        */
    }

}