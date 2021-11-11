package com.example.festivalapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.Objects;

public class DetailInfoMapFragment extends Fragment {
    private static final String TAG = "DetailInfoMapFragment";
    
    private Context mContext;
    private MapView mapView;
    
    private Bundle bundle;
    private String contentid;
    private double latY; //사용자 위치 : y좌표 = 위도 = longitude
    private double longX; //사용자 위치 : x좌표 = 경도 = latitude
    
    private FirebaseFirestore firestore= FirebaseFirestore.getInstance(); //FirebaseFirestore
    private CollectionReference eventsReference= firestore.collection("events");//firestore - events 참조

    public DetailInfoMapFragment() {
        // Required empty public constructor
        Log.e(TAG,"DetailInfoMapFragment()");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG,"onCreate()");

        bundle = getArguments();
        if (bundle != null) {
            contentid = getArguments().getString("contentid");
            Log.e(TAG, "contentid=" + contentid);

            longX = bundle.getDouble("longX");
            latY = bundle.getDouble("latY");
            Log.e(TAG, "onCreate()-longX: " + longX);
            Log.e(TAG, "onCreate()-latY: " + latY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG,"onCreateView()");

        if (bundle != null) {
            // Inflate the layout for this fragment
            //Context
            mContext = getActivity();
            Log.e(TAG, "getActivity()");

            //MapView 띄우기
            View root = inflater.inflate(R.layout.fragment_detail_info_map, container, false);
            Log.e(TAG, "inflate()");
            mapView = new MapView(mContext);
            Log.e(TAG, "MapView()");
            ViewGroup mapViewContainer = (ViewGroup)root.findViewById(R.id.detailmap_view);
            Log.e(TAG, "findViewById()");
            mapViewContainer.addView(mapView);
            Log.e(TAG, "addView()");
            //지도 중심점 변경
            Log.e(TAG, "onCreateView()-longX: " + longX);
            Log.e(TAG, "onCreateView()-latY: " + latY);
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latY, longX), true);
            // 줌 레벨 변경
            mapView.setZoomLevel(3, true);
            Log.e(TAG, "setMapCenterPoint()");

            /* 마커 생성 */
            //mapPoint
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latY, longX);
            //마커(Marker) 추가
            MapPOIItem marker = new MapPOIItem();
            marker.setItemName("축제 위치"); // =title
            marker.setTag(Integer.parseInt(contentid));
            marker.setMapPoint(mapPoint);
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
            //맵 뷰에 추가
            mapView.addPOIItem(marker);
            Log.e(TAG, "createMarker()");

            Log.e(TAG, "return root");
            return root;
        }
        else {
            Log.e(TAG, "return null");
            return null;
        }
    }
}