package com.example.festivalapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.festivalapp.MapFragment;
import com.example.festivalapp.R;
import com.example.festivalapp.activity.ConfigActivity;
import com.example.festivalapp.activity.LoginActivity;
import com.example.festivalapp.activity.MypageActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MainActivity extends ConfigActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    /* 사이드메뉴 */
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    //FirebaseFirestore
    private FirebaseFirestore firestore= FirebaseFirestore.getInstance();

    //Fragment
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private MapFragment mapFragment;

    //Data-Bundle
    private Bundle bundle;
    private ArrayList<String> contentIdList;
    private double latY; //사용자 위치 : y좌표 = 위도 = longitude
    private double longX; //사용자 위치 : x좌표 = 경도 = latitude
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar 상단 메뉴
        setToolbar();

        //화면
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

    //지도 지우기
    public void clearMap(){
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(mapFragment);
        fragmentTransaction.commit();
    }

    //화면 돌아올 때 - 지도 다시 불러오기
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

    // Toolbar
    public void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24); // 아이콘 설정

        // DrawerLayout 좌측 메뉴 drawer_layout
        drawerLayout = findViewById(R.id.drawer_layout);

        // NavigationView 좌측 메뉴 drawer_nav_menu
        navigationView = (NavigationView) findViewById(R.id.drawer_nav_menu);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        navigationView.setNavigationItemSelectedListener(this);
    }

    // Toolbar - 메뉴 선택 시 이벤트
    public boolean onNavigationItemSelected(@NotNull MenuItem item) {
        switch (item.getItemId()) {
            // drawer
            case R.id.menu_mypage:
                //지도 지우기
                clearMap();
                //마이페이지 화면으로 이동
                onStartActivity(MypageActivity.class);
                break;
            case R.id.menu_bookmarks:
                Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_LONG).show();
                break;
            case R.id.menu_reviews:
                Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_LONG).show();
                break;
            case R.id.menu_logout:
                FirebaseAuth.getInstance().signOut();
                //로그인 화면으로 이동
                onStartActivity(LoginActivity.class);
                finish();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }
}