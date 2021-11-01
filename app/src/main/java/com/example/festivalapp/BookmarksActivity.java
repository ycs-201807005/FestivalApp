package com.example.festivalapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.festivalapp.activity.MypageActivity;
import com.example.festivalapp.activity.SignupActivity;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BookmarksActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<MarkerInfo> bmarksList = new ArrayList<MarkerInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            myStartActivity(SignupActivity.class);
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener((task) -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {

                        } else {
                            myStartActivity(MypageActivity.class);
                        }
                    }
                } else {

                }
            });

            db.collection("users").document(user.getUid()).collection("bookmarks")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                ArrayList<MarkerInfo> bookmarkList = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    String contentId = document.getData().get("contentid").toString();
                                    String image = document.getData().get("firstimage").toString();
                                    String title = document.getData().get("title").toString();
                                    String eventplace = document.getData().get("eventplace").toString();

                                    MarkerInfo markerInfo = new MarkerInfo(contentId, image, title, eventplace);
                                    bmarksList.add(markerInfo);
                                }
                                recyclerView = findViewById(R.id.recyclerBmarks);
                                recyclerView.setLayoutManager(new LinearLayoutManager(BookmarksActivity.this));
                                RecyclerView.Adapter mAdapter = new com.example.festivalapp.BookmarksAdapter(BookmarksActivity.this, bmarksList);
                                recyclerView.setAdapter(mAdapter);
                            } else {

                            }
                        }
                    });
        }

        /*for (int i = 0; i < 15; i++) {
            String contentId = "" + i;
            String title = "Title" + i + " : test";
            String eventplace = "eventplace " + i + "-" + i;
            MarkerInfo markerInfo = new MarkerInfo(contentId, title, eventplace);
            bmarksList.add(markerInfo);

        }*/
    }
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}