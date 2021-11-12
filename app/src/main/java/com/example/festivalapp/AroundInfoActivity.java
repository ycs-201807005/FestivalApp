package com.example.festivalapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.example.festivalapp.activity.ConfigActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import net.daum.android.map.MapViewEventListener;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.Objects;

public class AroundInfoActivity extends ConfigActivity {
    private static final String TAG = "AroundInfoActivity";

    private TabLayout tabs;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private WifiFragment wifiFragment;
    private ToiletFragment toiletFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_around_info);

        //intent
        Intent intent = getIntent();
        double latY = intent.getDoubleExtra("latY",0.0);
        double longX = intent.getDoubleExtra("longX",0.0);
        //bundle
        Bundle bundle = new Bundle();
        bundle.putDouble("longX",longX);
        bundle.putDouble("latY",latY);

        //fragment
        wifiFragment = new WifiFragment();
        toiletFragment = new ToiletFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        wifiFragment.setArguments(bundle);
        fragmentTransaction.add(R.id.container, wifiFragment);

        //tab
        tabs = findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("Public Wifi"));
        tabs.addTab(tabs.newTab().setText("Public Toilet"));
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Fragment selected = null;
                if(position == 0) {
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.remove(toiletFragment);
                    fragmentTransaction.commit();
                    selected = wifiFragment;
                }
                else if(position == 1) {
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.remove(wifiFragment);
                    fragmentTransaction.commit();
                    selected = toiletFragment;
                }
                selected.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.container, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        fragmentTransaction.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Map Clear
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(wifiFragment);
        fragmentTransaction.remove(toiletFragment);
        fragmentTransaction.commit();
    }

}