package com.example.festivalapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db;
    private DocumentReference docRef;

    private SwipeRefreshLayout refresh_layout;
    private RecyclerView recyclerView;
    private ArrayList<MarkerInfo> bmarksList = new ArrayList<MarkerInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        db = FirebaseFirestore.getInstance();
        docRef = db.collection("users").document(user.getUid());
        if (user == null) {
            myStartActivity(SignupActivity.class);
        } else {
            getBookmarkLists();
        }

        refresh_layout = findViewById(R.id.refresh_layout);
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /* swipe 시 진행할 동작 */
                getBookmarkLists();
                /* 업데이트가 끝났음을 알림 */
                refresh_layout.setRefreshing(false);
            }
        });

    }
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    private void getBookmarkLists(){
        bmarksList.clear();

        docRef.collection("bookmarks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
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
}