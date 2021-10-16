package com.example.festivalapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BookmarksActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<MarkerInfo> bmarksList = new ArrayList<MarkerInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        for (int i=0 ; i<15; i++){
            String contentId = ""+i;
            String title = "Title" + i + " : test";
            String eventplace = "eventplace " + i + "-" + i;
            MarkerInfo markerInfo = new MarkerInfo(contentId,title,eventplace);
            bmarksList.add(markerInfo);
        }

        recyclerView = findViewById(R.id.recyclerBmarks);
        recyclerView.setLayoutManager(new LinearLayoutManager(BookmarksActivity.this));
        RecyclerView.Adapter mAdapter = new com.example.festivalapp.BookmarksAdapter(BookmarksActivity.this, bmarksList);
        recyclerView.setAdapter(mAdapter);
    }
}