package com.example.festivalapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FestivalDialogFragment extends DialogFragment {
    private static final String TAG = "MarkersDialogFragment";
    private Activity activity;
    private MainActivity mainActivity;

    private FirebaseFirestore firestore= FirebaseFirestore.getInstance(); //FirebaseFirestore
    private CollectionReference eventsReference= firestore.collection("events");//firestore - events 참조

    private String contentid;

    public FestivalDialogFragment(Context mContext) {
        // Required empty public constructor
        activity = (Activity) mContext;
        mainActivity = (MainActivity) activity;
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
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Bundle args = getArguments();
        contentid = args.getString("contentid");

        View view = inflater.inflate(R.layout.fragment_festival_dialog, container, false);
        view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //지도 지우기
                mainActivity.clearMap();
                //값-화면 전환
                Intent intent = new Intent(activity, DetailInfoActivity.class);
                intent.putExtra("contentid", contentid);
                activity.startActivity(intent);
                //다이얼로그 닫기
                getDialog().dismiss();
            }
        });

        /*데이터 가져오기*/
        Query query = eventsReference.whereEqualTo("contentid", contentid);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String contentid = (String) document.get("contentid");
                        String image = (String) document.get("firstimage"); //imgvImage
                        String title = (String) document.get("title");
                        String eventstartdate = (String) document.get("eventstartdate");
                        String eventenddate = (String) document.get("eventenddate");
                        String eventplace = (String) document.get("eventplace");

                        //이미지
                        ImageView imgvImage = view.findViewById(R.id.imgvImage);
                        Glide.with(view).load(image).override(view.getWidth(), view.getHeight()).into(imgvImage);
                        //제목
                        TextView tvTitle = view.findViewById(R.id.tvTitle);
                        tvTitle.setText(title);
                        //기간
                        TextView tvEventdate = view.findViewById(R.id.tvEventdate);
                        tvEventdate.setText(eventstartdate + " ~ " + eventenddate);
                        //장소
                        TextView tvEventplace = view.findViewById(R.id.tvEventplace);
                        tvEventplace.setText(eventplace);

                    }
                } else {
                    Log.e(TAG, "query - task failed");
                }

            }
        });

        return view;
    }
}