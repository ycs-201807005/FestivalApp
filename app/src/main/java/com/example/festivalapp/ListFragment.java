package com.example.festivalapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;

public class ListFragment extends Fragment {
    private static final String TAG = "ListFragment";

    private Bundle bundle;
    //private ArrayList<String> contentIdList= new ArrayList<String>();

    private RecyclerView recyclerView;
    private ArrayList<FestivalInfo> festivalsList = new ArrayList<FestivalInfo>();
    private FirebaseFirestore firestore= FirebaseFirestore.getInstance(); //FirebaseFirestore
    private CollectionReference eventsReference= firestore.collection("events");//firestore - events 참조

    private ArrayList<HashMap<String, String>> mapList = new ArrayList<HashMap<String, String>>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bundle = getArguments();
        if (bundle != null) {
            Log.e(TAG, "bundle!=null");
            //contentIdList = bundle.getStringArrayList("contentIdList");
            //Log.e(TAG, "contentIdList.size()=" + contentIdList.size());
            mapList = (ArrayList<HashMap<String, String>>) bundle.getSerializable("mapList");
            String first = mapList.get(0).get("contentid");
            Log.e(TAG,first);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e(TAG, "onCreateView()");

       // ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_list, container, false);

        View view = inflater.inflate(R.layout.fragment_list, container, false);
        Log.e(TAG, "inflate(R.layout.fragment_list)");
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        /*데이터 가져오기*/
        if (bundle != null) {
            /* contentIdList.add("주변에서 진행 중인 축제가 없습니다."); 처리 필요
            if (contentIdList.get(0).equals("주변에서 진행 중인 축제가 없습니다.")) {
                Log.e(TAG, "주변에서 진행 중인 축제가 없습니다.");
            }*/
            String first = mapList.get(0).get("contentid");
            Log.e(TAG,first);
            if (first.equals("주변에서 진행 중인 축제가 없습니다.")) {
                Log.e(TAG, "주변에서 진행 중인 축제가 없습니다.");
            }
            else {
                for (int i = 0; i < mapList.size(); i++) {
                    //Log.e(TAG, contentIdList.get(i));
                    //String contentid = contentIdList.get(i);
                    String contentid = mapList.get(i).get("contentid");
                    String dist =  mapList.get(i).get("dist");
                    int d = Integer.parseInt(dist);
                    Query query = eventsReference.whereEqualTo("contentid", contentid);
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String firstimage = (String) document.get("firstimage");
                                    String title = (String) document.get("title");
                                    String eventstartdate = (String) document.get("eventstartdate");
                                    String eventenddate = (String) document.get("eventenddate");
                                    String eventplace = (String) document.get("eventplace");
                                    //festivalsList.add(new FestivalInfo(contentid, firstimage, title, eventstartdate, eventenddate, eventplace));
                                    Log.e(TAG,contentid + " " + dist + " " + firstimage + " " + title + " " + eventstartdate + eventenddate + eventplace);
                                    festivalsList.add(new FestivalInfo(contentid, d, firstimage, title, eventstartdate, eventenddate, eventplace));
                                    Log.e(TAG, "festivalsList 추가 contentid - " + contentid);
                                }
                            } else {
                                Log.e(TAG, "query - task failed");
                            }

                            Log.e(TAG, "festivalsList -- 추가 --");
                            Collections.sort(festivalsList, cmpAsc); //sort
                            ListsAdapter adapter = new ListsAdapter(getActivity(), festivalsList);
                            recyclerView.setAdapter(adapter);
                            Log.e(TAG, "recyclerView.setAdapter(adapter)");
                            Log.e(TAG, "festivalsList.size() --" + String.valueOf(festivalsList.size()) + "--");
                        }
                    });
                }
                /*
                    Collections.sort(festivalsList, cmpAsc); //sort
                    Log.e(TAG, "festivalsList.size()-" + String.valueOf(festivalsList.size()));
                    for(int i=0; i<festivalsList.size(); i++){
                        Log.e(TAG, " index-" + i + " title-" + festivalsList.get(i).getTitle() + " dist-" + festivalsList.get(i).getDist());
                    }
                */
            }

        }
        Log.e(TAG, "return view");
        return view;
    }

    Comparator<FestivalInfo> cmpAsc = new Comparator<FestivalInfo>() {
        @Override
        public int compare(FestivalInfo o1, FestivalInfo o2) {
            return o1.compareTo(o2) ;
        }
    };
}