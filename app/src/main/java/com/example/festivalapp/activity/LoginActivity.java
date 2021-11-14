package com.example.festivalapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.festivalapp.AllFestivalInfoUpdate;
import com.example.festivalapp.GPSActivity;
import com.example.festivalapp.PasswordresetActivity;
import com.example.festivalapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends ConfigActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /* 자동 로그인 */
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user != null){
            if(user.getEmail().equals("test@manager.com")) {
                onStartActivity(AllFestivalInfoUpdate.class); //Firebase 전체 데이터 관리
                finish();
            }
            else {
                onStartActivity(GPSActivity.class); //사용자 현채 위치 가져오기
                finish();
            }
        }

        findViewById(R.id.btnSignIn).setOnClickListener(onClickListener);
        findViewById(R.id.btnSignUp).setOnClickListener(onClickListener);
        findViewById(R.id.btnForgot).setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnSignIn:
                    login();
                    break;
                case R.id.btnSignUp:
                    onStartActivity(SignupActivity.class);
                    break;
                case R.id.btnForgot:
                    onStartActivity(PasswordresetActivity.class);
                    break;
            }
        }
    };

    /* 로그인 */
    private void login() {
        String editEmail = ((EditText) findViewById(R.id.editEmail)).getText().toString();
        String editPW = ((EditText) findViewById(R.id.editPW)).getText().toString();

        if (editEmail.length() > 0 && editPW.length() > 0) {
            mAuth.signInWithEmailAndPassword(editEmail, editPW)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                startToast("Login Success.");

                                if(editEmail.equals("test@manager.com")) {
                                    onStartActivity(AllFestivalInfoUpdate.class); //Firebase 전체 데이터 관리
                                    finish();
                                }
                                else {
                                    onStartActivity(GPSActivity.class); //사용자 현채 위치 가져오기
                                    finish();
                                }

                            } else {
                                if (task.getException() != null) {
                                    startToast(task.getException().toString());
                                }
                            }
                        }
                    });
        }else {
            startToast("Check Email or Password.");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        user = mAuth.getCurrentUser();
        if(user != null){
            if(user.getEmail().equals("test@manager.com")) {
                onStartActivity(AllFestivalInfoUpdate.class); //Firebase 전체 데이터 관리
                finish();
            }
            else {
                onStartActivity(GPSActivity.class); //사용자 현채 위치 가져오기
                finish();
            }
        }

    }

    /* Toast Message*/
    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}