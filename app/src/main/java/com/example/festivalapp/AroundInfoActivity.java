package com.example.festivalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.ViewGroup;

import com.example.festivalapp.activity.ConfigActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import net.daum.android.map.MapViewEventListener;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.Objects;

public class AroundInfoActivity extends ConfigActivity implements MapView.POIItemEventListener{
    private MapView mapView;
    private ViewGroup mapViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_around_info);

        mapView = new MapView(this);

        mapViewContainer = (ViewGroup) findViewById(R.id.tab1);
        mapViewContainer.addView(mapView);

        mapView.setPOIItemEventListener(this);

        // 중심점 변경
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(/*latY, longX*/37.53737528, 127.00557633), true);

        // 줌 레벨 변경
        mapView.setZoomLevel(2, true);

        // 줌 인
        mapView.zoomIn(true);

        // 줌 아웃
        mapView.zoomOut(true);

        /* Marker 생성 */
        //mapPoint
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(/*latY, longX*/37.53737528, 127.00557633);
        //마커(Marker) 추가
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("축제 위치"); // =title
        marker.setTag(0);

        marker.setMapPoint(mapPoint);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        //맵 뷰에 추가
        mapView.addPOIItem(marker);
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

    @Override
    protected void onPause() {
        super.onPause();
        //Map Clear
        mapViewContainer.removeAllViews();
    }
}