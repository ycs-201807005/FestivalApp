package com.example.festivalapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.festivalapp.BookmarksActivity;
import com.example.festivalapp.R;
import com.example.festivalapp.ReviewListActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MypageActivity extends ConfigActivity {
    private TextView tvName, tvEmail;
    private Button btnBookmarks;

    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        firebaseFirestore = FirebaseFirestore.getInstance();

        //버튼 - 이벤트리스너
        findViewById(R.id.btnLogout).setOnClickListener(onClickListener);
        findViewById(R.id.btnPwdreset).setOnClickListener(onClickListener);
        findViewById(R.id.btnBookmarks).setOnClickListener(onClickListener);
        findViewById(R.id.btnReviews).setOnClickListener(onClickListener);

        //회원정보 출력!
        tvName = (TextView) findViewById(R.id.tvName);
        tvEmail = (TextView) findViewById(R.id.tvEmail);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").
                document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener((task) -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if(documentSnapshot != null) {
                    if(documentSnapshot.exists()) {
                        tvName.setText(documentSnapshot.getData().get("name").toString());
                        tvEmail.setText(documentSnapshot.getData().get("email").toString());
                    } else {

                    }
                }
            } else {

            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnPwdreset:
                    //로그인 화면으로 이동
                    onStartActivity(LoginActivity.class);
                    finish();
                    break;
                case R.id.btnLogout:
                    FirebaseAuth.getInstance().signOut();
                    //로그인 화면으로 이동
                    onStartActivity(LoginActivity.class);
                    finish();
                    break;
                case R.id.btnBookmarks:
                    //북마크 목록으로 이동
                    onStartActivity(BookmarksActivity.class);
                    break;
                case R.id.btnReviews:
                    //리뷰 목록으로 이동
                    onStartActivity(ReviewListActivity.class);
                    break;
                /*
                case R.id.btnUserDelete:
                    //회원 탈퇴 이동
                    onStartActivity(-.class);
                    break;*/
            }
        }
    };

}