package com.example.festivalapp;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.festivalapp.activity.ReviewWriteActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class DetailInfoActivity extends AppCompatActivity {
    private static final String TAG = "DetailInfoActivity";

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private DetailInfoMapFragment detailInfoMapFragment;

    /* Firebase */
    private FirebaseFirestore firestore= FirebaseFirestore.getInstance(); //FirebaseFirestore
    private CollectionReference eventsReference= firestore.collection("events");//firestore - events 참조

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_info);

        Bundle bundle = new Bundle();

        /* contentid 가져오기 */
        Intent intent = getIntent();
        String contentid = intent.getStringExtra("contentid");
        Log.e("실행","DetailInfoActivity:contentid-" + contentid);

        /* 위젯 가져오기 */
        ImageView imgvImage = (ImageView)findViewById(R.id.imgvImage);
        TextView tvTitle = (TextView)findViewById(R.id.tvTitle);
        TextView tvOverview = (TextView)findViewById(R.id.tvOverview);
        TextView tvInfoContents = (TextView)findViewById(R.id.tvInfoContents);
        TextView tvMapContents = (TextView)findViewById(R.id.tvMapContents);

        /* contentid 문서 가져오기 */
        Query query = eventsReference.whereEqualTo("contentid", contentid);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.e(TAG, "query.get(): " + task.isSuccessful());
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        /* contentid 필드 값 set */
                        //대표이미지
                        String imgUrl = document.getData().get("firstimage").toString();
                        Glide.with(imgvImage).load(imgUrl).override(imgvImage.getWidth(), imgvImage.getHeight()).into(imgvImage);

                        //제목 및 개요
                        tvTitle.setText(document.getData().get("title").toString());
                        tvOverview.setText(document.getData().get("overview").toString());

                        //소개정보
                        String agelimit = document.getData().get("agelimit").toString(); //관람 가능연령
                        tvInfoContents.setText("[ 관람 가능연령 ]\n " + agelimit);

                        String playtime = document.getData().get("playtime").toString(); //공연시간
                        String program = document.getData().get("program").toString(); //행사 프로그램
                        String subevent = document.getData().get("subevent").toString(); //부대행사
                        tvInfoContents.append("\n\n[ 공연시간 ]\n " + playtime + "\n");
                        tvInfoContents.append("\n[ 행사 프로그램 ]\n " + program + "\n");
                        tvInfoContents.append("\n[ 부대행사 ]\n " + subevent);

                        String usetimefestival = document.getData().get("usetimefestival").toString(); //이용요금
                        String discountinfofestival = document.getData().get("discountinfofestival").toString(); //할인정보
                        String bookingplace = document.getData().get("bookingplace").toString(); //예매처
                        String homepage = document.getData().get("homepage").toString(); //홈페이지
                        tvInfoContents.append("\n\n[ 이용요금 ]\n " + usetimefestival + "\n");
                        tvInfoContents.append("\n[ 할인정보 ]\n " + discountinfofestival + "\n");
                        tvInfoContents.append("\n[ 예매처 ]\n " + bookingplace + "\n");
                        tvInfoContents.append("\n[ 홈페이지 ]\n " + homepage);

                        String sponsor1 = document.getData().get("sponsor1").toString(); //주최자
                        String sponsor2 = document.getData().get("sponsor2").toString(); //주관사
                        tvInfoContents.append("\n\n[ 주최자 ]\n " + sponsor1+ "\n");
                        tvInfoContents.append("\n[ 주관사 ]\n " + sponsor2);

                        //위치정보
                        String addr1 = document.getData().get("addr1").toString(); //주소
                        String addr2 = document.getData().get("addr2").toString(); //상세주소
                        String eventplace = document.getData().get("eventplace").toString(); //행사 장소
                        String placeinfo = document.getData().get("placeinfo").toString(); //행사 위치 안내
                        tvMapContents.setText("\n\n[ 주소 ]\n " + addr1 + " " + addr2+ "\n");
                        tvMapContents.append("\n[ 행사 장소 ]\n " + eventplace+ "\n");
                        tvMapContents.append("\n[ 행사 위치 안내 ]\n " + placeinfo);


                        /* Bundle */
                        bundle.putString("contentid", contentid);
                        bundle.putDouble("latY", Double.parseDouble(document.getData().get("mapy").toString()));
                        bundle.putDouble("longX", Double.parseDouble(document.getData().get("mapx").toString()));
                        Log.e(TAG,"query-mapy"+document.getData().get("mapy").toString());
                        Log.e(TAG,"query-mapx"+document.getData().get("mapx").toString());

                        Log.e(TAG,"query success");
                        detailInfoMapFragment = new DetailInfoMapFragment();
                        detailInfoMapFragment.setArguments(bundle);

                        fragmentManager = getSupportFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragmentdetailmap, detailInfoMapFragment);

                        fragmentTransaction.commit();
                        Log.e(TAG,"commit()");
                    }
                } else {
                    Log.e(TAG,"query failed");
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(detailInfoMapFragment);
        fragmentTransaction.commit();
    }

    public void ReviewWriteclick(View view) {
        Intent intent = new Intent(getApplicationContext(), ReviewWriteActivity.class);
        startActivity(intent);
    }
}