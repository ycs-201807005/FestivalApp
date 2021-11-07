package com.example.festivalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.festivalapp.FirebaseFuncs;
import com.example.festivalapp.R;
import com.example.festivalapp.ReviewInfo;
import com.example.festivalapp.activity.ConfigActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReviewWriteActivity extends ConfigActivity {
    private static final String TAG = "ReviewWriteActivity";

    private FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ReviewInfo reviewInfo;

    private Button btnRvInsert;
    private RatingBar ratingBar;
    private TextView tvTitle, tvToday;
    private EditText txtreview;

    private String writer, contentid, title, writedate, contents;
    private double rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);

        writer = user.getUid();

        Intent intent = getIntent();
        contentid = intent.getStringExtra("contentid");
        title = intent.getStringExtra("title");
        tvTitle = (TextView)findViewById(R.id.tvTitle);
        tvTitle.setText(title);

        ratingBar = (RatingBar)findViewById(R.id.ratingBar);

        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
        writedate = sdformat.format(new Date());
        tvToday = (TextView)findViewById(R.id.tvToday);
        tvToday.setText("date : " + writedate);

        txtreview = (EditText) findViewById(R.id.txtreview);

        //리뷰 등록 버튼
        btnRvInsert = (Button)findViewById(R.id.btnRvInsert);
        btnRvInsert.setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnRvInsert:
                    Log.e(TAG,"btnRvInsert 리뷰 등록 버튼 Click");

                    //별점
                    rating = ratingBar.getRating();
                    Log.e(TAG,"rating=" + rating);

                    //작성 내용
                    contents = String.valueOf(txtreview.getText());
                    Log.e(TAG,"contents=" + contents);

                  /* Firebase - 리뷰 등록 */
                    reviewInfo = new ReviewInfo(writer, writedate, contentid, title, contents, rating);
                    db.collection("reviews").add(reviewInfo)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    //Upload성공
                                    Log.e(TAG, "Review Upload Success.");
                                    Toastmsg("리뷰가 등록되었습니다.");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //Upload실패
                                    Log.e(TAG, "Review Upload Fail.");
                                    Toastmsg("리뷰가 등록에 실패했습니다.");
                                }
                            });

                    /* @Review List Activity 업데이트 */
                    // 화면 다시 보여질때 업데이트해주면 될 듯

                    finish();
                    break;

            }
        }
    };
}