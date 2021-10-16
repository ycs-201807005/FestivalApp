package com.example.festivalapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.festivalapp.FirebaseFuncs;
import com.example.festivalapp.R;

public class LoginActivity extends ConfigActivity {
    private Button btnSignIn, btnSignUp;
    private String id,pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /* 추가 - 자동 로그인 기능 */
        boolean login = false;
        if(login){
            onStartActivity(HomeActivity.class);
            finish();
        }

        /* 버튼 클릭 이벤트 */
        //로그인 버튼 클릭
        btnSignIn = (Button)findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                //1. 입력 여부 확인
                id="test";
                pwd="1234";
                //2. Firebase 회원 확인 -@ ID/Pwd 전달!
                FirebaseFuncs firebaseFuncs = new FirebaseFuncs();
                boolean isMember = firebaseFuncs.isMember(id, pwd);
                if(isMember){
                    onStartActivity(HomeActivity.class);
                    finish();
                }
                else{
                    //"존재하지 않는 회원입니다."
                }
            }
        });
        /*
        //회원가입 버튼 클릭
        btnSignUp = (Button)findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStartActivity(JoinActivity.class);
            }
        });*/
    }

}