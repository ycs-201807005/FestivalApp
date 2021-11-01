package com.example.festivalapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MarkersDialogFragment extends DialogFragment {
    private static final String TAG = "MarkersDialogFragment";

    private Fragment fragment;
    private FirebaseFirestore firestore= FirebaseFirestore.getInstance(); //FirebaseFirestore
    private CollectionReference eventsReference= firestore.collection("events");//firestore - events 참조

    private ArrayList<MarkerInfo> markerInfos;
    private ArrayList<String> redundancies;

    public MarkersDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_layout, container, false);

        markerInfos = new ArrayList<MarkerInfo>(); //WriteInfo 리스트
        redundancies = new ArrayList<String>(); //중복 리스트
        Bundle args = getArguments();
        redundancies = args.getStringArrayList("redundancies");

        RecyclerView dialogRecyclerView; // RecyclerView

        dialogRecyclerView = (RecyclerView) view.findViewById(R.id.dialogRecyclerView);
        dialogRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        /*데이터 가져오기*/
        for (int i = 0; i < redundancies.size(); i++) {
            Log.e("실행", redundancies.get(i));
            String contentid = redundancies.get(i); //1930109
            Query query = eventsReference.whereEqualTo("contentid", contentid);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String contentid = (String) document.get("contentid");
                            String image = (String) document.get("firstimage");
                            String title = (String) document.get("title");
                            String eventplace = (String) document.get("eventplace");
                            markerInfos.add(new MarkerInfo(contentid, image, title, eventplace));
                        }
                    } else {
                        Log.e(TAG, "query - task failed");
                    }

                    if(contentid==redundancies.get(redundancies.size()-1)){
                        Log.e(TAG, String.valueOf(markerInfos.size()));
                        DialogRecyclerAdapter adapter = new DialogRecyclerAdapter(getContext(), markerInfos);
                        dialogRecyclerView.setAdapter(adapter);
                        Log.e(TAG, "recyclerView.setAdapter(adapter)");
                    }
                }
            });
        }

        return view;
    }
}