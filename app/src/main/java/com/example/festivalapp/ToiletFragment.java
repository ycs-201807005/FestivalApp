package com.example.festivalapp;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;


public class ToiletFragment extends Fragment implements MapView.POIItemEventListener{
    private static final String TAG = "ToiletFragment";

    private DatabaseReference mDatabase;

    private Context mContext;
    private MapView mapView;
    private ViewGroup mapViewContainer;

    private Bundle bundle;
    private Location mlocation;//사용자 위치
    private double latY; //사용자 위치 : y좌표 = 위도 = longitude
    private double longX; //사용자 위치 : x좌표 = 경도 = latitude

    public ToiletFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
        mlocation = new Location("");
        if (bundle != null) {
            Log.e(TAG, "bundle!=null");
            longX = bundle.getDouble("longX");
            latY = bundle.getDouble("latY");
            Log.e(TAG, "ToiletFragment:onCreate()-longX: " + longX);
            Log.e(TAG, "ToiletFragment:onCreate()-latY: " + latY);

            mlocation.setLongitude(longX);
            mlocation.setLatitude(latY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (bundle != null) {
            //Context
            mContext = getActivity();

            View root = inflater.inflate(R.layout.fragment_toilet, container, false);
            mapView = new MapView(mContext);
            ViewGroup mapViewContainer = (ViewGroup)root.findViewById(R.id.map_view);
            mapViewContainer.addView(mapView);
            // 중심점 변경
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latY, longX), true);
            // 줌 레벨 변경
            mapView.setZoomLevel(4, true);
            // 줌 인
            mapView.zoomIn(true);
            // 줌 아웃
            mapView.zoomOut(true);
            // Marker 생성
            //mapPoint
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latY, longX);
            //마커(Marker) 추가
            MapPOIItem marker = new MapPOIItem();
            marker.setItemName("축제 위치"); // =title
            marker.setTag(0);
            marker.setMapPoint(mapPoint);
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
            //맵 뷰에 추가
            mapView.addPOIItem(marker);

            toiletMarkers();

            return root;
        }
        else {
            return null;
        }
    }

    private void toiletMarkers(){
        mDatabase = FirebaseDatabase.getInstance().getReference("around");
        mDatabase.child("toilet").child("items").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                // 파이어베이스 데이터베이스의 데이터를 받아옴
                Log.e(TAG,dataSnapshot.getKey());

                // 반복문으로 값 추출
                HashMap<String, String> hashMap = new HashMap<String, String>();
                String key="";
                String val="";
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //키-값 가져오기
                    key = snapshot.getKey();
                    if(key.equals("longitude") || key.equals("latitude") || key.equals("toiletNm")){
                        val = snapshot.getValue(String.class);
                        if(val!=""){
                            Log.e(TAG,key);
                            Log.e(TAG,val);

                            hashMap.put(snapshot.getKey(),snapshot.getValue(String.class));
                        }
                    }
                }
                //ToiletInfo
                //ToiletInfo toiletInfo = new ToiletInfo(hashMap.get("toiletNm"),hashMap.get("rdnmadr"),hashMap.get("openTime"),hashMap.get("latitude"),hashMap.get("longitude"));
                if(hashMap.size()==3) {
                    double lon = Double.parseDouble(hashMap.get("longitude"));//x
                    double lat = Double.parseDouble(hashMap.get("latitude"));//y
                    Location location = new Location("");
                    location.setLongitude(lon);
                    location.setLatitude(lat);

                    //거리 : mlocation~location <= 500m
                    //float distance = mlocation.distanceTo(location); //m단위
                    //if (distance <= 5000) {
                        //mapPoint
                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(lat, lon);
                        //마커(Marker) 추가
                        MapPOIItem marker = new MapPOIItem();
                        marker.setItemName(hashMap.get("toiletNm")); // =title
                        marker.setTag(Integer.parseInt(dataSnapshot.getKey()));
                        marker.setMapPoint(mapPoint);
                        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                        //맵 뷰에 추가
                        mapView.addPOIItem(marker);
                    //}
                }
                hashMap.clear();
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                // 디비를 가져오던중 에러 발생
            }

        });
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }
}