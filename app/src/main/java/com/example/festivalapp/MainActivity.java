package com.example.festivalapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.festivalapp.MapFragment;
import com.example.festivalapp.R;
import com.example.festivalapp.activity.ConfigActivity;
import com.example.festivalapp.activity.LoginActivity;
import com.example.festivalapp.activity.MypageActivity;
import com.example.festivalapp.activity.ReviewWriteActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends ConfigActivity {
    private static final String TAG = "MainActivity";

    private FirebaseFirestore firestore= FirebaseFirestore.getInstance(); //FirebaseFirestore

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private MapFragment mapFragment;

    private Bundle bundle;
    private ArrayList<String> contentIdList;
    private double latY; //사용자 위치 : y좌표 = 위도 = longitude
    private double longX; //사용자 위치 : x좌표 = 경도 = latitude

    private final int gallery_image = 200;
    private ImageView imageview;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnLogout).setOnClickListener(onClickListener);
        findViewById(R.id.btnMypage).setOnClickListener(onClickListener);

        Display display = getWindowManager().getDefaultDisplay();
        FragmentContainerView fragmentContainerViewmap = (FragmentContainerView)findViewById(R.id.fragmentmap);
        FragmentContainerView fragmentContainerViewlist = (FragmentContainerView)findViewById(R.id.fragmentlist);
        Point size = new Point();
        display.getRealSize(size);
        fragmentContainerViewmap.getLayoutParams().height = size.y;
        fragmentContainerViewlist.getLayoutParams().height = size.y;

        Log.e("실행", "MainActivity:onCreate()");

        fragmentManager = getSupportFragmentManager();
        Log.e("실행", "MainActivity:getSupportFragmentManager()");
        fragmentTransaction = fragmentManager.beginTransaction();
        Log.e("실행", "MainActivity:beginTransaction()");
        mapFragment = new MapFragment();

        //Bundle
        bundle = new Bundle();
        contentIdList = getIntent().getStringArrayListExtra("contentIdList");
        longX = getIntent().getDoubleExtra("longX",37.56667092127576);
        latY = getIntent().getDoubleExtra("latY",126.97804107475767);
        bundle.putStringArrayList("contentIdList",contentIdList);
        Log.e("실행", "MainActivity:bundle-putStringArrayList()-" + contentIdList.size());
        bundle.putDouble("longX",longX);
        bundle.putDouble("latY",latY);

        mapFragment.setArguments(bundle);
        Log.e("실행", "MainActivity:mapFragment.setArguments(bundle)");
        fragmentTransaction.replace(R.id.fragmentmap, mapFragment);
        Log.e("실행", "MainActivity:add()");

        ListFragment listFragment = new ListFragment();
        listFragment.setArguments(bundle);
        Log.e("실행", "MainActivity:listFragment.setArguments(bundle)");
        fragmentTransaction.replace(R.id.fragmentlist, listFragment);
        Log.e("실행", "MainActivity:add()");

        fragmentTransaction.commit();
        Log.e("실행", "MainActivity:commit()");

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnLogout:
                    FirebaseAuth.getInstance().signOut();
                    //로그인 화면으로 이동
                    onStartActivity(LoginActivity.class);
                    finish();
                    break;
                case R.id.btnMypage:
                    //지도 지우기
                    clearMap();
                    //마이페이지 화면으로 이동
                    onStartActivity(MypageActivity.class);
                    break;
            }
        }
    };

    public void clearMap(){
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(mapFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fragmentTransaction = fragmentManager.beginTransaction();
        mapFragment.setArguments(bundle);
        Log.e("실행", "MainActivity:mapFragment.setArguments(bundle)");

        fragmentTransaction.add(R.id.fragmentmap, mapFragment);
        Log.e("실행", "MainActivity:add()");
        fragmentTransaction.commitAllowingStateLoss();
        Log.e("실행", "MainActivity:commit()");
    }


}