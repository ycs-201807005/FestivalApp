package com.example.festivalapp;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.festivalapp.activity.ConfigActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReviewWriteActivity extends ConfigActivity {
    private static final String TAG = "ReviewWriteActivity";

    private FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ReviewInfo reviewInfo;

    private Button btnRvInsert;
    private RatingBar ratingBar;
    private TextView tvTitle, tvToday, txtSnsLink;
    private EditText txtreview;

    private String writer, contentid, title, writedate, snslink, contents;
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
        txtSnsLink = (TextView)findViewById(R.id.txtSnsLink);

        //?????? ?????? ??????
        btnRvInsert = (Button)findViewById(R.id.btnRvInsert);
        btnRvInsert.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnRvInsert:
                    Log.e(TAG,"btnRvInsert ?????? ?????? ?????? Click");

                    //??????
                    rating = ratingBar.getRating();
                    Log.e(TAG,"rating=" + rating);

                    //?????? ??????
                    contents = String.valueOf(txtreview.getText());
                    Log.e(TAG,"contents=" + contents);

                    //??????
                    snslink = String.valueOf(txtSnsLink.getText());

                  /* Firebase - ?????? ?????? */
                    reviewInfo = new ReviewInfo(writer, writedate, contentid, title, contents, snslink, rating);
                    db.collection("reviews").add(reviewInfo)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    //Upload??????
                                    Log.e(TAG, "Review Upload Success.");
                                    Toastmsg("????????? ?????????????????????.");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //Upload??????
                                    Log.e(TAG, "Review Upload Fail.");
                                    Toastmsg("????????? ????????? ??????????????????.");
                                }
                            });

                    /* @Review List Activity ???????????? */
                    // ?????? ?????? ???????????? ????????????????????? ??? ???

                    finish();
                    break;

            }
        }
    };
}