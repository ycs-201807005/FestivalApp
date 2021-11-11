package com.example.festivalapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.festivalapp.activity.ConfigActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ReviewListActivity extends ConfigActivity {
    private static final String TAG = "ReviewListActivity";

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String writer;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ReviewInfo reviewInfo;
    private SwipeRefreshLayout refresh_layout;
    private RecyclerView recyclerView;
    private ArrayList<ReviewInfo> reviewsList = new ArrayList<ReviewInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);

        recyclerView = findViewById(R.id.recyclerReviews);
        refresh_layout = findViewById(R.id.refresh_layout);
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /* swipe 시 진행할 동작 */
                getReviewList();
                /* 업데이트가 끝났음을 알림 */
                refresh_layout.setRefreshing(false);
            }
        });

        //writer
        writer = user.getUid();

        getReviewList();

    }

    Comparator<ReviewInfo> cmpAsc = new Comparator<ReviewInfo>() {
        @Override
        public int compare(ReviewInfo o1, ReviewInfo o2) {
            return o1.compareTo(o2) ;
        }
    };

    @Override
    protected void onRestart() {
        super.onRestart();

        /* reviews 가져오기 */
        getReviewList();
    }

    private void getReviewList(){
        reviewsList.clear();
        /* 사용자가 작성한 리뷰 리스트 가져오기 */
        db.collection("reviews").whereEqualTo("writer", writer)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String reviewid = document.getId(); //Review Doc Id
                                String writedate = document.getData().get("writedate").toString();
                                String writer = document.getData().get("writer").toString();
                                String contentid = document.getData().get("contentid").toString();
                                String title = document.getData().get("title").toString();
                                String contents = document.getData().get("contents").toString();
                                double rating = (double) document.getData().get("rating");

                                reviewInfo = new ReviewInfo(reviewid, writer, writedate, contentid, title,contents,rating);
                                reviewsList.add(reviewInfo);
                            }

                            if(reviewsList.size()!=0){
                                Collections.sort(reviewsList, cmpAsc); //sort

                                recyclerView.setLayoutManager(new LinearLayoutManager(ReviewListActivity.this));
                                RecyclerView.Adapter mAdapter = new ReviewListAdapter(ReviewListActivity.this, reviewsList);
                                recyclerView.setAdapter(mAdapter);
                            }

                        } else {
                            //task fail
                        }
                    }
                });
    }

}