package com.example.festivalapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.Objects;

public class MapFragment extends Fragment implements MapView.POIItemEventListener{
    private static final String TAG = "MapFragment";

    private Context mContext;
    private MainActivity mainActivity;
    private MapView mapView;
    private ArrayList<MapPOIItem> mapPOIItems = new ArrayList<MapPOIItem>();

    private Bundle bundle;
    private ArrayList<String> contentIdList= new ArrayList<String>();
    private double latY; //사용자 위치 : y좌표 = 위도 = longitude
    private double longX; //사용자 위치 : x좌표 = 경도 = latitude

    private FirebaseFirestore firestore= FirebaseFirestore.getInstance(); //FirebaseFirestore
    private CollectionReference eventsReference= firestore.collection("events");//firestore - events 참조

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("실행", "MapFragment:onCreate()");

        bundle = getArguments();
        if (bundle != null) {
            Log.e(TAG, "bundle!=null");
            contentIdList = bundle.getStringArrayList("contentIdList");
            Log.e(TAG, "contentIdList.size()=" + contentIdList.size());

            longX = bundle.getDouble("longX");
            latY = bundle.getDouble("latY");
            Log.e(TAG, "MapFragment:onCreate()-longX: " + longX);
            Log.e(TAG, "MapFragment:onCreate()-latY: " + latY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("실행", "MapFragment:onCreateView()");

        if (bundle != null) {
            //Context
            mContext = getActivity();
            mainActivity = (MainActivity) getActivity();
            Log.e("실행", "MapFragment:getActivity()");

            //MapView 띄우기
            View root = inflater.inflate(R.layout.fragment_map, container, false);
            Log.e("실행", "MapFragment:inflate()");
            mapView = new MapView(mContext);
            Log.e("실행", "MapFragment:MapView()");
            ViewGroup mapViewContainer = (ViewGroup)root.findViewById(R.id.map_view);
            Log.e("실행", "MapFragment:findViewById()");
            mapViewContainer.addView(mapView);
            Log.e("실행", "MapFragment:addView()");

            //MapView-Marker Event 감지
            mapView.setPOIItemEventListener(this);
            Log.e("실행", "MapFragment:setPOIItemEventListener()");

            //지도 중심점 변경
            Log.e(TAG, "MapFragment:onCreateView()-longX: " + longX);
            Log.e(TAG, "MapFragment:onCreateView()-latY: " + latY);
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latY, longX), true);
            // 줌 레벨 변경
            mapView.setZoomLevel(7, true);
            Log.e("실행", "MapFragment:setMapCenterPoint()");
            // 줌 인
            mapView.zoomIn(true);
            // 줌 아웃
            mapView.zoomOut(true);
            

            /* Marker 생성 */
            createMarkers();
            Log.e("실행", "MapFragment:createMarkers()");

            Log.e("실행", "MapFragment:return root");
            return root;
        }
        else {
            Log.e("실행", "MapFragment:return null");
            return null;
        }
    }

    /* Markers */
    private void createMarkers() {
        mapPOIItems.clear();

        /* contentIdList.add("주변에서 진행 중인 축제가 없습니다."); 처리 필요 */
        if(contentIdList.get(0).equals("주변에서 진행 중인 축제가 없습니다.")){
            Log.e("실행", "contentIdList.get(0)-" + contentIdList.get(0));
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latY,longX);
            MapPOIItem marker = new MapPOIItem();
            marker.setItemName("주변 축제가 없습니다."); // =title
            marker.setTag(0);
            marker.setMapPoint(mapPoint);
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
            //맵 뷰에 추가
            mapView.addPOIItem(marker);
        }
        else{
            /* contentid가 Firebase에 저장되어 있을 때만 */
            //contentid 검색
            for (int i = 0; i < contentIdList.size(); i++) {
                Log.e("실행", contentIdList.get(i));
                String contentid = contentIdList.get(i); //1930109
                Query query = eventsReference.whereEqualTo("contentid", contentid);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e(TAG, "MapFragment:marker-for(document): " + document.getString("mapx"));
                                /* 마커 생성 */
                                //mapPoint
                                double mapy = Double.parseDouble(Objects.requireNonNull(document.getString("mapy")));
                                double mapx = Double.parseDouble(Objects.requireNonNull(document.getString("mapx")));
                                MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(mapy, mapx);
                                //마커(Marker) 추가
                                MapPOIItem marker = new MapPOIItem();
                                marker.setItemName(document.getString("title")); // =title
                                marker.setTag(Integer.parseInt(Objects.requireNonNull(document.getString("contentid"))));
                                Log.e("실행", "MapFragment:marker.setTag() = " + document.getString("contentid"));
                                Log.e("실행", "MapFragment:marker.setItemName() = " + document.getString("title"));
                                Log.e("실행", "MapFragment:marker.mapPoint = " + mapx + ", " + mapy);
                                marker.setMapPoint(mapPoint);
                                marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                                marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                                //맵 뷰에 추가
                                mapView.addPOIItem(marker);
                                mapPOIItems.add(marker);
                            }
                        } else {
                            Log.e("실행", "query - task failed");
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
        //사용자가 MapView 에 등록된 POIItem 아이콘(마커)를 터치한 경우 호출
        ArrayList<String> redundancies = new ArrayList<String>(); //중복 리스트

        /* contentIdList.add("주변에서 진행 중인 축제가 없습니다."); 처리 필요 */
        if(mapPOIItem.getTag()==0){

        }
        else{
            /*마커 중복 검사*/
            //클릭된 마커의 좌표 비교 : contentid -> mapx, mapy
            MapPoint callmapPoint = mapPOIItem.getMapPoint(); //선택된 마커 포인트
            double callLat = callmapPoint.getMapPointGeoCoord().latitude; //선택된 마커 latitude-위도 -y좌표 :37
            double callLong = callmapPoint.getMapPointGeoCoord().longitude; //선택된 마커 longitude-경도 -x좌표 :126
            Log.e("실행", "callLat-위도 -y좌표 :37=" + callLat);
            Log.e("실행", "callLong-경도 -x좌표 :126=" + callLong);

            //존재하는 marker 중에서 같은 좌표가 있는 경우
            for (int m = 0; m < mapPOIItems.size(); m++) {
                MapPoint mapPoint = mapPOIItems.get(m).getMapPoint();
                double y = mapPoint.getMapPointGeoCoord().latitude;
                double x = mapPoint.getMapPointGeoCoord().longitude;
                Log.e("실행", "callLat-위도 -y좌표 :37=" + y);
                Log.e("실행", "callLong-경도 -x좌표 :126=" + x);

                if (callLat == y && callLong == x) {
                    redundancies.add(String.valueOf(mapPOIItems.get(m).getTag()));
                }
            }
            Log.e("실행", String.valueOf(redundancies.size()));

            if (redundancies.size() > 1) {
                //RecyclerDialog출력 - redundancies 전달!
                showAlertDialogMarkers(redundancies);
            } else {
                String contentid = "" + mapPOIItem.getTag();
                Log.e("실행", "contentid=" + contentid);
                showAlertDialogFestival(contentid);
            }
        }
    }

    /* 위치 중복 축제 목록 Dialog*/
    private void showAlertDialogMarkers(ArrayList<String> redundancies) {
        MarkersDialogFragment dialog = new MarkersDialogFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("redundancies",redundancies);
        dialog.setArguments(args); // 데이터 전달
        dialog.show(getActivity().getSupportFragmentManager(),"tag");
    }

    /*선택 축제 정보 Dialog*/
    private void showAlertDialogFestival(String contentid) {
        FestivalDialogFragment dialog = new FestivalDialogFragment(mContext);
        Bundle args = new Bundle();
        args.putString("contentid",contentid);
        dialog.setArguments(args); // 데이터 전달
        dialog.show(getActivity().getSupportFragmentManager(),"tag");
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

    /* 화면 전환 */
    private void onStartActivity(String contentid) {
        Intent intent = new Intent(mContext, DetailInfoActivity.class);
        intent.putExtra("contentid",contentid);
        startActivity(intent);//실행
    }

}