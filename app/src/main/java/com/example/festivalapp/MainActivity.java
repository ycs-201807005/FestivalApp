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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends ConfigActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    /* ??????????????? */
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    //?????????
    private SearchView searchBar;

    //Fragment
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private MapFragment mapFragment;

    //Data-Bundle
    private Bundle bundle, bundle1;
    private ArrayList<String> contentIdList;
    private ArrayList<HashMap<String, String>> mapList;
    private double latY; //????????? ?????? : y?????? = ?????? = longitude
    private double longX; //????????? ?????? : x?????? = ?????? = latitude

    /* Firebase : Firestore */
    FirebaseFirestore firestore = FirebaseFirestore.getInstance(); //FirebaseFirestore
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //FirebaseUser
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar ?????? ??????
        setToolbar();

        /* ????????? */
        searchBar = findViewById(R.id.searchView_search);
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.e(TAG,"??????-"+searchBar.isIconified());
                if(searchBar.isIconified()) {
                    searchBar.setIconified(false);
                    //Log.e(TAG,"??????-"+searchBar.isIconified());
                }
            }
        });

        int searchCloseButtonId = searchBar.getContext().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButton = (ImageView) this.searchBar.findViewById(searchCloseButtonId);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e(TAG,"????????????-"+searchBar.isIconified());
                searchBar.setIconified(true);
                //Log.e(TAG,"????????????-"+searchBar.isIconified());
            }
        });

        searchBar.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean b)
            {   //Log.e(TAG,"???????????????-"+searchBar.isIconified());
                if(searchBar.isIconified()) {
                    searchBar.setIconified(false);
                }
                //Log.e(TAG,"???????????????-"+searchBar.isIconified());
            }
        });

        // searchView Listener
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // ?????? ?????? ?????? ??? ??????
            @Override
            public boolean onQueryTextSubmit(String query) {
                //?????? ????????? ?????? ??????
                Location location = findGeoPoint(getApplicationContext(), query); //Location findGeoPoint()
                Log.e(TAG,"("+ location.getLatitude() + "," + location.getLongitude() + ")"); //(latY,longX)=(37,126)
                //?????? ??????
                List<Address> address=null;
                String addr = "????????? ?????? ??? ????????????.";
                Geocoder g = new Geocoder(getApplicationContext());
                try {
                    address = g.getFromLocation(location.getLatitude(),location.getLongitude(),10); //(latY,longX)=(37,126)
                    addr = address.get(0).getAddressLine(0);
                    Log.e(TAG, "[????????????] ?????? = " + addr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("?????? ?????? ??????");
                    builder.setMessage("?????? ????????? ???????????????.\n?????? ?????? : " + addr);
                    builder.setCancelable(true);
                    String finalAddr = addr;
                    if(finalAddr.equals("????????? ?????? ??? ????????????.")){
                        builder.setMessage(addr + "\n??? ????????? ????????? ??????????????????.");
                        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Log.e(TAG,"showDialogForLocationSave() - ??????");
                            }
                        });
                    }
                    else {
                        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Log.e(TAG,"showDialogForLocationSave() - ??????");
                                /* ?????? ????????? ?????? ?????? */
                                saveLocation(location.getLatitude(), location.getLongitude(), finalAddr); //(latY,longX)=(37,126)
                            }
                        });
                        builder.setNegativeButton("??????",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Log.e(TAG,"showDialogForLocationSave() - ??????");
                            }
                        });
                    }

                    builder.show(); //AlertDialog.Builder

                    return true;
                }

            }
            // ?????? ????????? ?????? ??? ??????
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        /* ??? ??? */
        Display display = getWindowManager().getDefaultDisplay();
        FragmentContainerView fragmentContainerViewmap = (FragmentContainerView)findViewById(R.id.fragmentmap);
        FragmentContainerView fragmentContainerViewlist = (FragmentContainerView)findViewById(R.id.fragmentlist);
        Point size = new Point();
        display.getRealSize(size);
        fragmentContainerViewmap.getLayoutParams().height = size.y-150;
        fragmentContainerViewlist.getLayoutParams().height = size.y-100;

        Log.e("??????", "MainActivity:onCreate()");

        /* ??????????????? */
        fragmentManager = getSupportFragmentManager();
        Log.e("??????", "MainActivity:getSupportFragmentManager()");
        fragmentTransaction = fragmentManager.beginTransaction();
        Log.e("??????", "MainActivity:beginTransaction()");

        //Bundle
        bundle = new Bundle();
        contentIdList = getIntent().getStringArrayListExtra("contentIdList");
        longX = getIntent().getDoubleExtra("longX",longX);
        latY = getIntent().getDoubleExtra("latY",latY);
        bundle.putStringArrayList("contentIdList",contentIdList);
        Log.e("??????", "MainActivity:bundle-putStringArrayList()-" + contentIdList.size());
        bundle.putDouble("longX",longX);
        bundle.putDouble("latY",latY);

        bundle1 = new Bundle();
        mapList = new ArrayList<HashMap<String, String>>();
        mapList = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("mapList");
        bundle1.putSerializable("mapList",mapList);

        //mapFragment
        mapFragment = new MapFragment();
        mapFragment.setArguments(bundle);
        Log.e("??????", "MainActivity:mapFragment.setArguments(bundle)");
        fragmentTransaction.replace(R.id.fragmentmap, mapFragment);
        Log.e("??????", "MainActivity:add()");

        //ListFragment
        ListFragment listFragment = new ListFragment();
        listFragment.setArguments(bundle1);
        Log.e("??????", "MainActivity:listFragment.setArguments(bundle)");
        fragmentTransaction.replace(R.id.fragmentlist, listFragment);
        Log.e("??????", "MainActivity:add()");

        fragmentTransaction.commit();
        Log.e("??????", "MainActivity:commit()");

    }

    //?????? ?????????
    public void clearMap(){
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(mapFragment);
        fragmentTransaction.commit();
    }

    //?????? ????????? ??? - ?????? ?????? ????????????
    @Override
    protected void onRestart() {
        super.onRestart();
        fragmentTransaction = fragmentManager.beginTransaction();
        mapFragment.setArguments(bundle);
        Log.e("??????", "MainActivity:mapFragment.setArguments(bundle)");

        fragmentTransaction.add(R.id.fragmentmap, mapFragment);
        Log.e("??????", "MainActivity:add()");
        fragmentTransaction.commitAllowingStateLoss();
        Log.e("??????", "MainActivity:commit()");
    }

    /* Toolbar - ??????????????? */
    public void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24); // ????????? ??????

        // DrawerLayout ?????? ?????? drawer_layout
        drawerLayout = findViewById(R.id.drawer_layout);

        // NavigationView ?????? ?????? drawer_nav_menu
        navigationView = (NavigationView) findViewById(R.id.drawer_nav_menu);
        //????????? ?????? ????????????
        firestore.collection("users").document(user.getUid()).get()
                .addOnCompleteListener((task) -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            String username = (String) document.get("name");
                            navigationView.getMenu().getItem(0).setTitle(username + " ???");
                        }
                    } else {
                    }
                });
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

    // Toolbar - ?????? ?????? ??? ?????????
    public boolean onNavigationItemSelected(@NotNull MenuItem item) {
        switch (item.getItemId()) {
            // drawer
            case R.id.menu_mypage:
                //?????? ?????????
                clearMap();
                //??????????????? ???????????? ??????
                onStartActivity(MypageActivity.class);
                break;
            case R.id.menu_bookmarks:
                //?????? ?????????
                clearMap();
                //????????? ???????????? ??????
                onStartActivity(BookmarksActivity.class);
                break;
            case R.id.menu_reviews:
                //?????? ?????????
                clearMap();
                //?????? ???????????? ??????
                onStartActivity(ReviewListActivity.class);
                break;
            case R.id.menu_logout:
                FirebaseAuth.getInstance().signOut();
                //????????? ???????????? ??????
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

    /* ?????? -> ?????? */
    public static Location findGeoPoint(Context mcontext, String address) {
        Location loc = new Location("");
        Geocoder coder = new Geocoder(mcontext);
        List<Address> addr = null;// ???????????? ?????? ??????????????? ????????? ????????????????????? ??????????????? ???????????? ?????? ???
        try {
            addr = coder.getFromLocationName(address, 5);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// ?????? ????????? ????????? ???????????? ?????? 1~5??? ????????? ??????
        if (addr != null) {
            for (int i = 0; i < addr.size(); i++) {
                Address lating = addr.get(i);
                double lat = lating.getLatitude(); // ??????????????????
                double lon = lating.getLongitude(); // ??????????????????
                loc.setLatitude(lat);
                loc.setLongitude(lon);
            }
        }
        return loc;
    }

    /* ????????? ?????? ?????? ?????? Firebase??? ?????? */
    private void  saveLocation(double latitude, double longitude, String addr){  //(latY,longX)=(37,126)
        Log.e(TAG,"user ="+user.getUid());
        try {
            firestore.collection("users").document(user.getUid()).update("mapx",longitude) //-?????? -x?????? :126
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
            firestore.collection("users").document(user.getUid()).update("mapy",latitude) //-?????? -y?????? :37
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
                            Toastmsg("????????? ?????????????????????.");
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
            Toastmsg("?????? ?????? ??????");

        }
    }

}