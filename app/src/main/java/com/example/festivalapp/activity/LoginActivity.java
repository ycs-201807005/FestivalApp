package com.example.festivalapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

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

        /* 로그인 상태 확인*/
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user != null){
            onStartActivity(MainActivity.class); //->HomeActivity
            finish();
        }

        findViewById(R.id.tvSignIn).setOnClickListener(onClickListener);
        findViewById(R.id.tvSignUp).setOnClickListener(onClickListener);
        findViewById(R.id.tvForgot).setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.linlayoutSignIn :
                case R.id.tvSignIn:
                    login();
                    break;
                case R.id.tvSignUp:
                    //myStartActivity(SignupActivity.class);
                    break;
                case R.id.tvForgot:
                    //myStartActivity(PasswordresetActivity.class);
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
                                onStartActivity(MainActivity.class); //->HomeActivity
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

    /* Toast Message*/
    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}