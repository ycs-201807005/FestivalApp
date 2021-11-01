package com.example.festivalapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.festivalapp.MapFragment;
import com.example.festivalapp.R;
import com.example.festivalapp.activity.ConfigActivity;
import com.example.festivalapp.activity.LoginActivity;
import com.example.festivalapp.activity.MypageActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ConfigActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    /* 사이드메뉴 */
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    //검색바
    private SearchView searchBar;

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

        /* 검색바 */
        searchBar = findViewById(R.id.searchView_search);
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.e(TAG,"클릭-"+searchBar.isIconified());
                if(searchBar.isIconified()) {
                    searchBar.setIconified(false);
                    //Log.e(TAG,"클릭-"+searchBar.isIconified());
                }
            }
        });

        int searchCloseButtonId = searchBar.getContext().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButton = (ImageView) this.searchBar.findViewById(searchCloseButtonId);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e(TAG,"닫기버튼-"+searchBar.isIconified());
                searchBar.setIconified(true);
                //Log.e(TAG,"닫기버튼-"+searchBar.isIconified());
            }
        });

        searchBar.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean b)
            {   //Log.e(TAG,"포커스변경-"+searchBar.isIconified());
                if(searchBar.isIconified()) {
                    searchBar.setIconified(false);
                }
                //Log.e(TAG,"포커스변경-"+searchBar.isIconified());
            }
        });

        // searchView Listener
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 검색 버튼 누를 시 호출
            @Override
            public boolean onQueryTextSubmit(String query) {
                //주소 전달
                //좌표로 변경
                //if(변경 가능)
                //위치를 변경하시겠습니까? Yes/No
                //ContentIdListActivity 부터 다시 불러오기

                //입력 주소로 좌표 구함
                Location location = findGeoPoint(getApplicationContext(), query);
                Log.e(TAG,"("+ location.getLatitude() + "," + location.getLongitude() + ")");
                //주소 구함
                List<Address> address=null;
                String addr;
                Geocoder g = new Geocoder(getApplicationContext());
                try {
                    address = g.getFromLocation(location.getLatitude(),location.getLongitude(),10); //(y,x)
                    addr = address.get(0).getAddressLine(0);
                    Log.e(TAG, "[현재위치] 주소 = " + addr);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("현재 위치 정보");
                    builder.setMessage("현재 위치가 설정됩니다.\n현재 주소 : " + addr);
                    builder.setCancelable(true);
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Log.e(TAG,"showDialogForLocationSave() - 확인");
                            /* 현재 사용자 위치 저장 */
                            saveLocation(location.getLatitude(), location.getLongitude(), addr);
                        }
                    });
                    builder.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
            // 검색 입력값 변경 시 호출
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        /* 화 면 */
        Display display = getWindowManager().getDefaultDisplay();
        FragmentContainerView fragmentContainerViewmap = (FragmentContainerView)findViewById(R.id.fragmentmap);
        FragmentContainerView fragmentContainerViewlist = (FragmentContainerView)findViewById(R.id.fragmentlist);
        Point size = new Point();
        display.getRealSize(size);
        fragmentContainerViewmap.getLayoutParams().height = size.y;
        fragmentContainerViewlist.getLayoutParams().height = size.y;

        Log.e("실행", "MainActivity:onCreate()");

        /* 프래그먼트 */
        fragmentManager = getSupportFragmentManager();
        Log.e("실행", "MainActivity:getSupportFragmentManager()");
        fragmentTransaction = fragmentManager.beginTransaction();
        Log.e("실행", "MainActivity:beginTransaction()");

        //Bundle
        bundle = new Bundle();
        contentIdList = getIntent().getStringArrayListExtra("contentIdList");
        longX = getIntent().getDoubleExtra("longX",longX);
        latY = getIntent().getDoubleExtra("latY",latY);
        bundle.putStringArrayList("contentIdList",contentIdList);
        Log.e("실행", "MainActivity:bundle-putStringArrayList()-" + contentIdList.size());
        bundle.putDouble("longX",longX);
        bundle.putDouble("latY",latY);

        //mapFragment
        mapFragment = new MapFragment();
        mapFragment.setArguments(bundle);
        Log.e("실행", "MainActivity:mapFragment.setArguments(bundle)");
        fragmentTransaction.replace(R.id.fragmentmap, mapFragment);
        Log.e("실행", "MainActivity:add()");

        //ListFragment
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

    /* Toolbar - 사이드메뉴 */
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
                //지도 지우기
                clearMap();
                //북마크 목록으로 이동
                onStartActivity(BookmarksActivity.class);
                break;
            case R.id.menu_reviews:
                //지도 지우기
                clearMap();
                //리뷰 목록으로 이동
                onStartActivity(ReviewListActivity.class);
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

    /* 주소 -> 좌표 */
    public static Location findGeoPoint(Context mcontext, String address) {
        Location loc = new Location("");
        Geocoder coder = new Geocoder(mcontext);
        List<Address> addr = null;// 한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 설
        try {
            addr = coder.getFromLocationName(address, 5);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// 몇개 까지의 주소를 원하는지 지정 1~5개 정도가 적당
        if (addr != null) {
            for (int i = 0; i < addr.size(); i++) {
                Address lating = addr.get(i);
                double lat = lating.getLatitude(); // 위도가져오기
                double lon = lating.getLongitude(); // 경도가져오기
                loc.setLatitude(lat);
                loc.setLongitude(lon);
            }
        }
        return loc;
    }

    /* 사용자 현재 위치 정보 Firebase에 저장 */
    private void  saveLocation(double longitude, double latitude, String addr){
        /* Firebase : Firestore */
        FirebaseFirestore firestore = FirebaseFirestore.getInstance(); //FirebaseFirestore
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //FirebaseUser
        Log.e(TAG,"user ="+user.getUid());
        try {
            firestore.collection("users").document(user.getUid()).update("mapx",longitude) //-경도 -x좌표 :126
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e(TAG,"Location:mapx Update Success.-" + longitude);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG,"Location:mapx Update Fail.");
                        }
                    });
            firestore.collection("users").document(user.getUid()).update("mapy",latitude) //-위도 -y좌표 :37
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e(TAG,"Location:mapy Update Success.-"+latitude);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG,"Location:mapy Update Fail.");
                        }
                    });
            firestore.collection("users").document(user.getUid()).update("address",addr)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e(TAG,"Location:address Update Success.-"+addr);
                            Toastmsg("위치가 저장되었습니다.");
                            finish();
                            onStartActivity(ContentIdListActivity.class);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG,"Location:address Update Fail.");
                        }
                    });
        }
        catch (Exception e){
            Toastmsg("위치 저장 실패");

        }
    }

}