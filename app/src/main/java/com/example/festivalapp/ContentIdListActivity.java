package com.example.festivalapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.festivalapp.activity.ConfigActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ContentIdListActivity extends ConfigActivity {
    private static final String TAG = "ContentIdListActivity";
    private static final int LOAD_SUCCESS = 101;

    /* 사용자 위치 */
    private double latY; //사용자 위치 : y좌표-변경가능 = 위도 = longitude
    private double longX; //사용자 위치 : x좌표-변경가능 = 경도 = latitude

    /* Api 요청 URL 필요 값 */
    /* 요청 URL 형식
    private String Request_URL = ServiceURL + ServiceNames[?] + ServiceKey + numOfRows + MobileOS + MobileApp + ... + "&_type=json";
    위치기반 정보 조회 : Request_URL + arrange + contentTypeId + longX + latY + radius + "&_type=json";*/
    //필수 값
    private String ServiceURL = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList";
    private String ServiceKey = "?ServiceKey=GOwGXYMF0dZEUBsPVgHahmtxS7o3Adj8eHoZaxIgVQ9G9BKoTWtukgD9xqiUoDuwcXn7oUXT0lmh99OjhRF1uw%3D%3D";
    private String numOfRows = "&numOfRows=100";
    private String MobileOS = "&MobileOS=AND";
    private String MobileApp = "&MobileApp=test_api_list";
    private String arrange = "&arrange=E"; //*E=거리순
    private String contentTypeId = "&contentTypeId=15";
    private String radius = "&radius=15000"; //반경 : Max값 20000m=20Km

    // 응답 값 - 반경 내 현재 진행 중인 행사 id 목록 - MainActivity로 전달
    private ArrayList<String> contentIdList = new ArrayList<String>();

    /* ProgressDialog */
    private ProgressDialog progressDialog;

    /* Firebase */
    private FirebaseFirestore firestore;
    private FirebaseUser user;
    private CollectionReference eventsReference;//firestore - events 참조

    private ArrayList<String> storecontentIds = new ArrayList<String>(); //파베에 저장되어 있는 현재 진행 중인 행사 id 목록
    private ArrayList<HashMap<String, String>> mapList = new ArrayList<HashMap<String, String>>();

    /**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG,"onCreate()");
        setContentView(R.layout.activity_wait);

        firestore = FirebaseFirestore.getInstance(); //FirebaseFirestore
        eventsReference= firestore.collection("events");
        Query query = eventsReference.whereEqualTo("running", "Y");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String contentid =  document.getData().get("contentid").toString();
                        storecontentIds.add(contentid); //파베에 저장되어 있는 현재 진행 중인 행사 id 목록
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        /* Firebase : Firestore - 사용자 현재 위치 좌표 가져오기 */
        user = FirebaseAuth.getInstance().getCurrentUser(); //FirebaseUser
        Log.e(TAG, "document: "+user.getUid());
        DocumentReference docRef = firestore.collection("users").document(user.getUid());
        Log.e(TAG, "DocumentReference: "+docRef);
        docRef.get().addOnCompleteListener((task) -> {
            Log.e(TAG, "task.isSuccessful(): "+task.isSuccessful());
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    if (document.exists()) {
                        longX = document.getDouble("mapx");
                        latY = document.getDouble("mapy");
                        Log.e(TAG, "longX: "+longX);
                        Log.e(TAG, "latY: "+latY);
                        //longX = 126.82152954401921; //longitude-경도 -x좌표 :126
                        //latY = 37.487340091293575; //latitude-위도 -y좌표 :37

                        /* 위치기반 조회 */
                        getJSON();

                    } else {
                        Log.e(TAG,"document == null");
                    }
                }
            } else {
                Log.e(TAG,"task.isFailed");
            }
        });

        /* ProgressDialog */
        progressDialog = new ProgressDialog(ContentIdListActivity.this);
        progressDialog.setMessage("Please wait!!");
        progressDialog.show();

    }


    private final MyHandler myhandler = new MyHandler(this);

    private class MyHandler extends Handler {
        private final WeakReference<ContentIdListActivity> weakReference;

        public MyHandler(ContentIdListActivity locIntroInfoActivity) {
            weakReference = new WeakReference<ContentIdListActivity>(locIntroInfoActivity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            ContentIdListActivity locIntroInfoActivity = weakReference.get();

            if (locIntroInfoActivity != null) {
                switch (msg.what) {
                    case LOAD_SUCCESS:
                        locIntroInfoActivity.progressDialog.dismiss();
                        onStartActivity();
                        break;

                }
            }
        }
    }

    private JSONObject jsonObject;
    public void RequestURLConn(String REQUEST_URL) {
        // 1. URL 연결
        try {
            URL url = new URL(REQUEST_URL);
            HttpURLConnection conn = null;
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responseStatusCode = conn.getResponseCode();

            // 2. 데이터 읽어오기
            InputStream inputStream;
            if (responseStatusCode == conn.HTTP_OK) {
                inputStream = conn.getInputStream();
            } else {
                inputStream = conn.getErrorStream();
            }
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader rd = new BufferedReader(inputStreamReader);
            StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                buffer.append(line);
            }
            // 3. 연결 끊기
            rd.close();
            conn.disconnect();
            // 4. JSON Object 형태로 변형
            jsonObject = new JSONObject(buffer.toString());

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void getJSON() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    /* (1) 위치기반 관광정보 조회 */
                    String loc_requestUrl = ServiceURL + ServiceKey + numOfRows + MobileOS + MobileApp + arrange + contentTypeId + "&mapX=" + longX + "&mapY=" + latY + radius + "&_type=json";
                    Log.e(TAG,"loc_requestUrl="+loc_requestUrl);
                    RequestURLConn(loc_requestUrl);
                    //배열로된 자료를 가져올때
                    JSONArray LocBased_Array = jsonObject.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item");  // JSONArray [{},{}] 생성
                        Log.e(TAG,"개수="+LocBased_Array.length()); // 위치 기반 관광 정보 조회 결과 개수
                        /* 값 없을 때 : No Value 처리 추가 */
                    if (LocBased_Array.length()>0){
                        Log.e(TAG,"storecontentIds-"+storecontentIds.size());
                        for (int i = 0; i < LocBased_Array.length(); i++) {
                            // JSONObject {},{} 추출
                            JSONObject Object = LocBased_Array.getJSONObject(i);
                            // JSONObject {"key", "value"} - value 추출
                            Log.e(TAG,Object.getString("contentid"));
                            String contentid = Object.getString("contentid");
                            //현재 진행중인 행사만 = firebase에 저장된 행사만 필터링
                            if(storecontentIds.contains(contentid)){ // 파베 현재 진행 중 목록에 포함되는 반경 내 행사 id
                                contentIdList.add(contentid);
                                Log.e(TAG, "MapFragment:marker-contentid: " + contentid);
                                Log.e(TAG, "MapFragment:marker-contentIdList: " + contentIdList.size());

                                String dist = Object.getString("dist");
                                HashMap<String, String> hashMap = new HashMap<String, String>();
                                hashMap.put("contentid", contentid);
                                hashMap.put("dist", dist);
                                mapList.add(hashMap);

                            }
                        }
                        if (contentIdList.size()==0){
                            contentIdList.add("주변에서 진행 중인 축제가 없습니다.");
                            Log.e(TAG,"주변에서 진행 중인 축제가 없습니다.");
                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            hashMap.put("contentid", "주변에서 진행 중인 축제가 없습니다.");
                            hashMap.put("dist", "0");
                            mapList.add(hashMap);
                        }
                        myhandler.sendEmptyMessage(LOAD_SUCCESS);
                    }
                    else {
                        contentIdList.add("주변에서 진행 중인 축제가 없습니다.");
                        Log.e(TAG,"주변에서 진행 중인 축제가 없습니다.");
                        HashMap<String, String> hashMap = new HashMap<String, String>();
                        hashMap.put("contentid", "주변에서 진행 중인 축제가 없습니다.");
                        hashMap.put("dist", "0");
                        mapList.add(hashMap);
                        myhandler.sendEmptyMessage(LOAD_SUCCESS);
                    }

                } catch (Exception e) {
                    contentIdList.add("주변에서 진행 중인 축제가 없습니다.");
                    Log.e(TAG,"API - 위치기반 조회 실패");
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("contentid", "주변에서 진행 중인 축제가 없습니다.");
                    hashMap.put("dist", "0");
                    mapList.add(hashMap);
                    myhandler.sendEmptyMessage(LOAD_SUCCESS);
                }

            }
        });

        thread.start();
    }

    private void onStartActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        Log.e(TAG,"contentIdList"+contentIdList.size());
        intent.putExtra("contentIdList",contentIdList);
        intent.putExtra("longX",longX);
        intent.putExtra("latY",latY );
        intent.putExtra("mapList",mapList );
        startActivity(intent);//실행
        finish();
    }
}
