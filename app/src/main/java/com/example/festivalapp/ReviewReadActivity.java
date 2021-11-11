package com.example.festivalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.festivalapp.activity.ConfigActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;

public class ReviewReadActivity extends ConfigActivity {
    private static final String TAG = "ReviewReadActivity";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String Uid;

    TextView btnRvDelete, tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_read);

        //삭제버튼 setVisible(t/f)
        Uid = user.getUid();
        btnRvDelete = (TextView)findViewById(R.id.btnRvDelete);

        Intent intent = getIntent();
        String reviewid = intent.getStringExtra("reviewid");
        Log.e(TAG,reviewid);

        // title - 상세 정보 이동
        tvTitle = (TextView)findViewById(R.id.tvTitle);
        String contentid = intent.getStringExtra("contentid");
        if(contentid!=null){
            Log.e(TAG,contentid);
            tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent1 = new Intent(ReviewReadActivity.this, DetailInfoActivity.class);
                    intent1.putExtra("contentid", contentid);
                    startActivity(intent1);
                    finish();
                }
            });
        }

        /* 선택한 리뷰 내용 가져오기 */
        DocumentReference docRef = db.collection("reviews").document(reviewid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String title = document.getData().get("title").toString();
                        tvTitle.setText(title);
                        double rating = (double) document.getData().get("rating");
                        Log.e(TAG, String.valueOf(rating));
                        RatingBar ratingBar = (RatingBar)findViewById(R.id.ratingBar);
                        ratingBar.setRating((float) rating);
                        String writedate = document.getData().get("writedate").toString();
                        TextView tvWritedate = (TextView)findViewById(R.id.tvWritedate);
                        tvWritedate.setText(writedate);
                        String writer = document.getData().get("writer").toString();
                        if(writer.equals(Uid)){
                            //리뷰 삭제 버튼 보이기
                            btnRvDelete.setVisibility(View.VISIBLE);
                        }
                        //사용자 이름 가져오기
                        db.collection("users").document(writer).get()
                                .addOnCompleteListener((task1) -> {
                                    if (task1.isSuccessful()) {
                                        DocumentSnapshot document1 = task1.getResult();
                                        if (document1 != null) {
                                            String writernmae = (String) document1.get("name");
                                            TextView tvWriter = (TextView)findViewById(R.id.tvWriter);
                                            tvWriter.setText("작성자 : " + writernmae);
                                        }
                                    } else {
                                    }
                                });
                        String contents = document.getData().get("contents").toString();
                        TextView txtreview = (TextView)findViewById(R.id.txtreview);
                        txtreview.setText(contents);

                    } else {
                        Log.d(TAG, "No such document");
                        Toastmsg("해당 글이 존재하지 않습니다.");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        /* 리뷰 삭제 하기 */
        btnRvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ReviewReadActivity.this);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.e(TAG,"showDialogForLocationSave() - 확인");
                        db.collection("reviews").document(reviewid).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                        Log.e(TAG, "리뷰 삭제");
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "Error deleting document", e);
                                    }
                                });
                    }
                });
                builder.show();
            }
        });
    }
}