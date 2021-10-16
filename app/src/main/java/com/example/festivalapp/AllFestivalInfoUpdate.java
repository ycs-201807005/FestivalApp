package com.example.festivalapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.festivalapp.activity.ConfigActivity;
import com.example.festivalapp.activity.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AllFestivalInfoUpdate extends ConfigActivity {
    private static final String TAG = "test_api_list";
    private static final int LOAD_SUCCESS = 101;

    /* Api 요청 URL 필요 값 */
    //필수 값
    private String ServiceURL = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/";
    private String[] ServiceNames = {"searchFestival" , "detailIntro", "detailCommon", "detailImage"}; // {행사정보 - 소개정보 - 공통정보 - 이미지정보 }
    private String ServiceKey = "?ServiceKey=GOwGXYMF0dZEUBsPVgHahmtxS7o3Adj8eHoZaxIgVQ9G9BKoTWtukgD9xqiUoDuwcXn7oUXT0lmh99OjhRF1uw%3D%3D";
    private String numOfRows = "&numOfRows=200";
    private String MobileOS = "&MobileOS=AND";
    private String MobileApp = "&MobileApp=test_api_list";
    private String contentTypeId = "&contentTypeId=15";

    //----------------------------------------------------------------------------------------------------------------------------------------

    /*현재 날짜*/
    private SimpleDateFormat sdformat = new SimpleDateFormat("yyyyMMdd");
    private String current = sdformat.format(new Date());
    private String today = String.valueOf(current);

    /* (1)행사정보 조회
    *  http://api.visitkorea.or.kr/openapi/service/rest/KorService/searchFestival
    *  ?serviceKey=-&numOfRows=800&pageNo=1&MobileOS=ETC&MobileApp=AppTest&arrange=A&listYN=Y&eventStartDate=20211007&eventEndDate=20211007
    *  : "진행 중"이거나 "진행 예정"인 행사들만 조회 됨 => "진행 예정"인 행사 제외 조건 달기!!
    */
    private String evtarrange = "&arrange=A"; //*A=제목순
    private String eventStartDate = "&eventStartDate=" + today;
    private String eventEndDate = "&eventEndDate=" + today;
    private String searchFestivalRequest = ServiceURL+ ServiceNames[0] + ServiceKey + numOfRows + MobileOS + MobileApp + evtarrange + eventStartDate + eventEndDate + "&_type=json";
    private String[] searchFestivalReply = {"contentid","eventstartdate","eventenddate", "firstimage", "firstimage2","mapx","mapy","title", "addr1", "addr2"};
    //{"콘텐츠ID","행사 시작일","행사 종료일", "대표이미지(원본)", "대표이미지(썸네일)", "GPS X좌표=경도=latitude", "GPS Y좌표=위도=longitude", "제목", "주소", "상세주소"}

    /* (2)소개정보 조회
    *  http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailIntro
    *  ?serviceKey=-&numOfRows=800&pageNo=1&MobileOS=ETC&MobileApp=AppTest&contentId=#&contentTypeId=15&_type=json"
    * */
    private String detailIntroRequest; // + "&contentId=2746930&contentTypeId=15&_type=json
    private String[] detailIntroReply = {"agelimit","bookingplace","discountinfofestival", "eventplace","placeinfo","playtime"
            ,"program","sponsor1","sponsor2","subevent","usetimefestival"};
    //detailIntroReply = {"가능연령","예매처","할인정보","행사 장소","행사장 위치안내","공연시간"
    //            ,"행사 프로그램","주최자 정보","주관사 정보","부대행사","이용요금"};

    /* (3)공통정보 조회
     *  http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailCommon
     *  ?serviceKey=-&numOfRows=800&pageNo=1&MobileOS=ETC&MobileApp=AppTest&contentId=#&contentTypeId=15&_type=json"
     * */
    private String detailCommonRequest; // + "&contentId=2746930&contentTypeId=15&_type=json"
    private String[] detailCommonReply = {"homepage", "overview"};
    //detailCmmomReply = {"홈페이지 주소", "개요"};

    /* (4)이미지정보 조회
     *  http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailImage
     *  ?serviceKey=-&numOfRows=10&pageNo=1&MobileOS=ETC&MobileApp=AppTest&contentId=2746930&imageYN=Y&subImageYN=Y
     * */
    private String detailImageRequest; // + "&contentId=2746930&imageYN=Y&subImageYN=Y&_type=json"
    private ArrayList<String> detailImageReply  = new ArrayList<String>();

    //----------------------------------------------------------------------------------------------------------------------------------------

    /* Firebase : Firestore */
    private FirebaseFirestore firestore;

    /* Widgets */
    private ProgressDialog progressDialog;
    private Button btnAllDelete,btnNewInsert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allfestivalinfoupdate);

        Log.e("now time", current);

        /* Firebase : Firestore */
        firestore = FirebaseFirestore.getInstance();

        /* Buttons */
        findViewById(R.id.btnAllDelete).setOnClickListener(onClickListener);
        findViewById(R.id.btnNewInsert).setOnClickListener(onClickListener);
        findViewById(R.id.btnMngLogout).setOnClickListener(onClickListener);
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnAllDelete:
                    /* 프로그레스 바 */
                    progressDialog = new ProgressDialog(AllFestivalInfoUpdate.this);
                    progressDialog.setMessage("Please wait!!");
                    progressDialog.show();
                    /* 기존 행사 정보 비우기 */
                    deleteDB();
                    break;
                case R.id.btnNewInsert:
                    /* 프로그레스 바 */
                    progressDialog = new ProgressDialog(AllFestivalInfoUpdate.this);
                    progressDialog.setMessage("Please wait!!");
                    progressDialog.show();
                    /* 행사 정보 json으로 조회 */
                    getJSON();
                    break;
                case R.id.btnMngLogout:
                    FirebaseAuth.getInstance().signOut();
                    onStartActivity(LoginActivity.class);
                    finish();
                    break;
                }
            }
        };


    /* MyHandler 핸들러 */
    private final mHandler myhandler = new mHandler(AllFestivalInfoUpdate.this);

    private class mHandler extends Handler {
        private final WeakReference<AllFestivalInfoUpdate> weakReference;

        public mHandler(AllFestivalInfoUpdate locIntroInfoActivity) {
            weakReference = new WeakReference<AllFestivalInfoUpdate>(locIntroInfoActivity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            AllFestivalInfoUpdate allFestivalInfoUpdate = weakReference.get();

            if (allFestivalInfoUpdate != null) {
                switch (msg.what) {
                    case LOAD_SUCCESS:
                        allFestivalInfoUpdate.progressDialog.dismiss();
                        //finish();
                        break;
                }
            }
        }
    }

    /* 기존 행사 정보 비움 */
    public void deleteDB() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //firestore - events 참조
                    CollectionReference eventsReference = firestore.collection("events");

                    /* (1) 행사정보 조회 - Delete */
                    //전체 삭제
                    eventsReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String docid = document.getId();
                                    eventsReference.document(docid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //Log.d(TAG, "DocumentSnapshot successfully deleted! >> " + docid);
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    //Log.w(TAG, "Error deleting document", e);
                                                }
                                            });
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                                finish();
                            }
                            myhandler.sendEmptyMessage(LOAD_SUCCESS);
                        }
                    });
                } catch (Exception e) {
                }
            }
        });
        //쓰레드 실행
        thread.start();
    }


    /* Url Repuest */
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

    /* 행사 정보 Uploader */
    public void getJSON() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //firestore - events 참조
                CollectionReference eventsReference = firestore.collection("events");

                /* (2) 행사정보 조회 - Upload*/
                /* EventsInfo 작성 */
                int searchFestivalRLength = searchFestivalReply.length;
                int detailIntroRLength = detailIntroReply.length;
                int detailCommonRLength = detailCommonReply.length;
                String contentId;

                try {
                    //현재날짜
                    Date currentDate = sdformat.parse(current);
                    //Log.e(TAG, "currentDate"+String.valueOf(currentDate));

                    //URL 요청
                    RequestURLConn(searchFestivalRequest);
                        //Log.e(TAG,"행사정보 요청 URL = " + searchFestivalRequest);

                    //1.Json - item 문서 가져오기
                    JSONArray eventInfoArray = jsonObject.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item");  // JSONArray [{},{}] 생성
                        Log.e(TAG, "eventInfo_Reply totalCnt : "+ eventInfoArray.length());

                    //2.행사정보 items 저장 (=item 반복)
                    for (int i = 0; i < eventInfoArray.length(); i++) {
                        /* EventsInfo 작성 */
                        Eventinfo eventinfo;
                        ArrayList<String> values = new ArrayList<String>();

                        //3.JSONObject {},{} = item 추출
                        JSONObject searchFestival_obj = eventInfoArray.getJSONObject(i);

                        //4.JSONObject {name1:value1,...} = Key-Value 추출 -> EventInfo 객체 만들어서 firestore에 Upload + 조건 : 진행중일것 => (현재날짜 < eventstartdate) 제외
                        Date evtstartDate = sdformat.parse(searchFestival_obj.getString("eventstartdate"));//eventstartdate
                        /* 진행 중인 행사인 경우 */
                        if(currentDate.compareTo(evtstartDate) >= 0){
                            //(2) 행사정보 조회 - 응답
                            for (int j = 0; j < searchFestivalRLength; j++) {
                                //Log.e(TAG,"searchFestivalRLength"+f);
                                if (searchFestival_obj.has(searchFestivalReply[j])==true && searchFestival_obj.getString(searchFestivalReply[j])!=""){
                                    values.add(searchFestival_obj.getString(searchFestivalReply[j])) ;
                                    Log.e(TAG,searchFestivalReply[j] +"-values.get(" + j +")-" +values.get(j));
                                }
                                else {
                                    values.add("");
                                    Log.e(TAG,searchFestivalReply[j] +"-values.get(" + j +")-" +values.get(j));
                                }
                            }

                            /* - contentid에 대해 - URL 요청 */
                            contentId = searchFestival_obj.getString("contentid");
                            Log.e(TAG,contentId);

                            /* (3) 소개정보 조회 */
                            detailIntroRequest = ServiceURL+ ServiceNames[1] + ServiceKey + numOfRows + MobileOS + MobileApp + "&contentId=" + contentId + contentTypeId + "&_type=json";
                            RequestURLConn(detailIntroRequest);
                                Log.e(TAG,"소개정보 요청 URL = " + detailIntroRequest);
                            JSONObject detailIntro_obj = jsonObject.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONObject("item");
                            //(3) 소개정보 조회 - 응답
                            for(int j=0; j<detailIntroRLength; j++){
                                if (detailIntro_obj.has(detailIntroReply[j])==true && detailIntro_obj.getString(detailIntroReply[j])!=""){
                                    values.add(detailIntro_obj.getString(detailIntroReply[j]));
                                    Log.e(TAG,detailIntroReply[j] +"-values.get(" + j +")-" +values.get(searchFestivalRLength+j));
                                }
                                else {
                                    values.add("");
                                    Log.e(TAG,detailIntroReply[j] +"-values.get(" + j +")-" +values.get(searchFestivalRLength+j));
                                }
                            }

                            /* (4) 공통정보 조회 */
                            detailCommonRequest = ServiceURL+ ServiceNames[2] + ServiceKey + numOfRows + MobileOS + MobileApp + "&contentId=" + contentId  + "&defaultYN=Y" + "&overviewYN=Y" + "&_type=json";
                            RequestURLConn(detailCommonRequest);
                                Log.e(TAG,"공통정보 요청 URL = " + detailCommonRequest);
                            JSONObject detailCommon_obj = jsonObject.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONObject("item");
                            // (4) 공통정보 조회 - 응답
                            for(int j=0; j<detailCommonRLength; j++){
                                if (detailCommon_obj.has(detailCommonReply[j])==true && detailCommon_obj.getString(detailCommonReply[j])!=""){
                                    values.add(detailCommon_obj.getString(detailCommonReply[j]));
                                    Log.e(TAG,detailCommonReply[j] +"-values.get(" + j +")-" +values.get(searchFestivalRLength+detailIntroRLength+j));
                                }
                                else {
                                    values.add("");
                                    Log.e(TAG,detailCommonReply[j] +"-values.get(" + j +")-" +values.get(searchFestivalRLength+detailIntroRLength+j));
                                }
                            }

                            /* (5) 이미지정보 조회 */
                            detailImageRequest = ServiceURL+ ServiceNames[3] + ServiceKey + numOfRows + MobileOS + MobileApp + "&contentId="  + contentId + "&imageYN=Y&subImageYN=Y&_type=json";
                            RequestURLConn(detailImageRequest);
                                Log.e(TAG,"이미지정보 요청 URL = " + detailImageRequest);
                            //이미지 정보 존재 여부 확인
                            String detailImageCnt = jsonObject.getJSONObject("response").getJSONObject("body").getString("totalCount");
                                Log.e(TAG,"detailImages 개수 : " + detailImageCnt);
                            if ( Integer.parseInt(detailImageCnt)<2){
                                // 이미지 정보 없는 경우
                                /* [Eventinfo] 1개 = [문서] 1개 = [행사] 1개 */
                                eventinfo = new Eventinfo(values);
                                //Log.e(TAG , "collection - events 생성");
                            }
                            else {
                                // 이미지 정보 있는 경우
                                //이미지 아이템 배열
                                ArrayList<String> detailImages = new ArrayList<String>();
                                    //Log.e(TAG,"detailImages 이미지 저장할 리스트 만듬");
                                JSONArray detailImageArray = jsonObject.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item");
                                for (int item=0; item<detailImageArray.length(); item++){
                                    JSONObject detailImage_obj = detailImageArray.getJSONObject(item);
                                    detailImages.add(detailImage_obj.getString("originimgurl"));
                                    //Log.e(TAG,"originimgurl=");
                                    //Log.e(TAG,"detailImages.get("+item+")="+detailImages.get(item));
                                }
                                /* [Eventinfo] 1개 = [문서] 1개 = [행사] 1개 */
                                eventinfo = new Eventinfo(values, detailImages);
                                //Log.e(TAG , "collection - 이미지 리스트 포함 events 생성");
                            }

                            /* Firabase : Firestore에 Upload*/
                            firestore.collection("events").add(eventinfo)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            //Upload성공
                                            Log.e(TAG, "EventInfo Upload Success.");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //Upload실패
                                            Toastmsg("EventInfo Upload Fail.");
                                            finish();
                                        }
                                    });
                            Log.e(TAG, "저장 [" +  i + "]");
                        } // if end : 현재 진행 중
                        else {
                        } // else end
                    } //for end : 행사정보 조회 - Upload
                //try end
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                Log.e(TAG,"EventInfo Upload End.");
                myhandler.sendEmptyMessage(LOAD_SUCCESS);
            }
        });
        //쓰레드 실행
        thread.start();
    }
}
