package com.example.festivalapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReviewListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<ReviewInfo> reviewsList = new ArrayList<ReviewInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);

        for (int i=0 ; i<15; i++){
            String writedate = "2021-10-10";
            String rid = "RID-"+i;
            String contentId = ""+i;
            String title = "Title" + i + " : test";
            String eventplace = "eventplace " + i + "-" + i;
            String star = "4";
            ReviewInfo reviewInfo = new ReviewInfo(writedate, rid,contentId,title, "My Review contents. Very good.My Review contents. Very good.My Review contents. Very good.",eventplace,star);
            reviewsList.add(reviewInfo);
        }

        recyclerView = findViewById(R.id.recyclerReviews);
        recyclerView.setLayoutManager(new LinearLayoutManager(ReviewListActivity.this));
        RecyclerView.Adapter mAdapter = new ReviewListAdapter(ReviewListActivity.this, reviewsList);
        recyclerView.setAdapter(mAdapter);
    }
}