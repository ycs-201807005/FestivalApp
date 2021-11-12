package com.example.festivalapp;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;

public class WifiFragment extends Fragment implements MapView.POIItemEventListener{
    private static final String TAG = "WifiFragment";

    private Context mContext;
    private MapView mapView;
    private ViewGroup mapViewContainer;

    private Bundle bundle;
    private double latY; //사용자 위치 : y좌표 = 위도 = longitude
    private double longX; //사용자 위치 : x좌표 = 경도 = latitude

    public WifiFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
        if (bundle != null) {
            Log.e(TAG, "bundle!=null");
            longX = bundle.getDouble("longX");
            latY = bundle.getDouble("latY");
            Log.e(TAG, "WifiFragment:onCreate()-longX: " + longX);
            Log.e(TAG, "WifiFragment:onCreate()-latY: " + latY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (bundle != null) {
            //Context
            mContext = getActivity();

            View root = inflater.inflate(R.layout.fragment_wifi, container, false);
            mapView = new MapView(mContext);
            ViewGroup mapViewContainer = (ViewGroup)root.findViewById(R.id.map_view);
            mapViewContainer.addView(mapView);
            // 중심점 변경
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latY, longX), true);
            // 줌 레벨 변경
            mapView.setZoomLevel(2, true);
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

            return root;
        }
        else {
            return null;
        }
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