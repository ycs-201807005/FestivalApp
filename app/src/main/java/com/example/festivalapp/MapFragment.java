package com.example.festivalapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

        /* contentIdList.add("주변에서 진행 중인 축제가 없습니다."); 처리 필요 */

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
            // Inflate the layout for this fragment
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

            // 커스텀 말풍선 등록
            mapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());
            Log.e("실행", "MapFragment:setCalloutBalloonAdapter()");
            //MapView-Marker Event 감지
            mapView.setPOIItemEventListener(this);
            Log.e("실행", "MapFragment:setPOIItemEventListener()");

            //지도 중심점 변경
            Log.e(TAG, "MapFragment:onCreateView()-longX: " + longX);
            Log.e(TAG, "MapFragment:onCreateView()-latY: " + latY);
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latY, longX), true);
            // 줌 레벨 변경
            mapView.setZoomLevel(8, true);
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
                            marker.setTag(Integer.parseInt(Objects.requireNonNull(document.getString("contentid")))); //tag=Cursor의 Row 번호 저장 : " row는 0부터 시작 "
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

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        /* 말풍선 클릭 시 */
        Log.e("실행", "MapFragment:onCalloutBalloonOfPOIItemTouched()");
        Log.e("실행", "Clicked " + mapPOIItem.getItemName() + " Callout Balloon");
        Log.e("실행", "MapFragment: contentid = " + mapPOIItem.getTag()); // =contentid

        // 상세페이지로 contentid 넘겨주기
        String contentid = ""+mapPOIItem.getTag();

        //지도 지우기
        mainActivity.clearMap();

        onStartActivity(contentid);

        /*
        cursor.close();
        Log.e("실행", "MapFragment:onCalloutBalloonOfPOIItemTouched() - cursor.close() ");
        db.close();
        Log.e("실행", "MapFragment:onCalloutBalloonOfPOIItemTouched() - db.close() ");
        */
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

    /* custom 말풍선 Class*/
    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {
        private RecyclerView dialogRecyclerView; // RecyclerView
        public Dialog dialog; // 출력할 Dialog 객체
        private ArrayList<MarkerInfo> markerInfos = new ArrayList<MarkerInfo>(); //WriteInfo 리스트

        private final View mCalloutBalloon;
        private int position;

        public CustomCalloutBalloonAdapter() {
            mCalloutBalloon = getLayoutInflater().inflate(R.layout.custom_callout_balloon, null);
        }

        ArrayList<String> redundancies = new ArrayList<String>(); //중복 리스트
        @Override
        public View getCalloutBalloon(MapPOIItem mapPOIItem) {
            //마커 클릭 시 표시할 뷰 (말풍선)
            Log.e("실행", "CustomCalloutBalloonAdapter:getCalloutBalloon()");

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
                double y= mapPoint.getMapPointGeoCoord().latitude;
                double x= mapPoint.getMapPointGeoCoord().longitude;
                Log.e("실행", "callLat-위도 -y좌표 :37=" + y);
                Log.e("실행", "callLong-경도 -x좌표 :126=" + x);

                if (callLat==y && callLong==x) {
                    redundancies.add(String.valueOf(mapPOIItems.get(m).getTag()));
                }
            }


            Log.e("실행", String.valueOf(redundancies.size()));

            if (redundancies.size() > 1) {
                //RecyclerDialog출력 - redundancies 전달!
                //showAlertDialogMarkers();
                return null;
            } else {
                String contentid = "" + mapPOIItem.getTag();
                Log.e("실행", "contentid=" + contentid);

                //말풍선 생성
                ((ImageView) mCalloutBalloon.findViewById(R.id.badge)).setImageResource(R.drawable.ic_launcher);
                ((TextView) mCalloutBalloon.findViewById(R.id.title)).setText(mapPOIItem.getItemName());
                eventsReference.whereEqualTo("contentid", contentid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ((TextView) mCalloutBalloon.findViewById(R.id.desc)).setText(document.getString("eventplace")); //=eventplace
                            }
                        } else {
                            //contentid에 해당하는 축제가 없음
                        }
                    }
                });
                return mCalloutBalloon;
            }
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            Log.e("실행", "CustomCalloutBalloonAdapter:getPressedCalloutBalloon()");
            return null;
        }

        /*Dialog*/
        private void showAlertDialogMarkers() { //->MainActivity에서 출력?
            //Display display = getWindowManager().getDefaultDisplay();
            //Point size = new Point();
            dialog = new Dialog(mContext);

            //display.getRealSize(size);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

            LayoutInflater inf = getLayoutInflater();
            View dialogView = inf.inflate(R.layout.dialog_layout, null);
            // Dialog layout 선언

            lp.copyFrom(dialog.getWindow().getAttributes());
            //int width = size.x;
            //lp.width = width * 80 / 100; // 사용자 화면의 80%
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // 높이는 내용 전체 높이만큼

            dialog.setContentView(dialogView); // Dialog에 선언했던 layout 적용
            dialog.setCanceledOnTouchOutside(true); // 외부 touch 시 Dialog 종료

            dialog.getWindow().setAttributes(lp); // 지정한 너비, 높이 값 Dialog에 적용

        /*
        다음 4줄의 코드는 RecyclerView를 정의하기 위한 View, Adapter선언 코드이다.
        1. RecyclerView id 등록
        2. 수직방향으로 보여줄 예정이므로 LinearLayoutManager 등록
           2차원이면 GridLayoutManager 등 다른 Layout을 선택
        3. adapter에 topic Array 넘겨서 출력되게끔 전달
        4. adapter 적용
        */
            dialogRecyclerView = (RecyclerView) dialogView.findViewById(R.id.dialogRecyclerView);
            dialogRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            DialogRecyclerAdapter adapter = new DialogRecyclerAdapter(/*this,*/markerInfos);
            dialogRecyclerView.setAdapter(adapter);
            dialog.show(); // Dialog 출력
        }
    }

}