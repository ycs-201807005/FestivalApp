package com.example.festivalapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.festivalapp.Memberinfo;
import com.example.festivalapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends ConfigActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.btnSignup).setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnSignup:
                    signup();
                    break;
            }
        }
    };

    private void signup() {
        String email = ((EditText)findViewById(R.id.editEmail)).getText().toString();
        String password = ((EditText)findViewById(R.id.editPwd)).getText().toString();
        String passwordCheck = ((EditText) findViewById(R.id.editPwdCheck)).getText().toString();
        String name = ((EditText) findViewById(R.id.editName)).getText().toString();

        if (email.length() > 0 && password.length() > 0 && passwordCheck.length() > 0 && name.length() > 0) {
            if (password.equals(passwordCheck)) {
                /* 사용자 추가 - Authentication */
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    /* 사용자 정보 추가 - Firestore */
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    Memberinfo memberinfo = new Memberinfo(name, 37.566741959771605, 126.97787155945724, "서울특별시 중구 명동 세종대로 110 : 서울특별시청", email);
                                    if(user != null) {
                                        db.collection("users").document(user.getUid()).set(memberinfo)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toastmsg("Sign Up Success.");
                                                        //onStartActivity(LoginActivity.class);
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toastmsg("Profile Update Fail.");
                                                    }
                                                });
                                    }
                                } else {
                                    if (task.getException() != null) {
                                        Toastmsg(task.getException().toString());
                                    }
                                }
                            }
                        });
            } else {
                Toastmsg("Passwords do not match.");
            }
        }else {
            Toastmsg("Check Email or Password.");
        }
    }


}