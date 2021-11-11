package com.example.festivalapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.festivalapp.activity.ConfigActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DetailInfoActivity extends ConfigActivity {
    private static final String TAG = "DetailInfoActivity";

    private CheckBox btnBook;
    private TextView btnRvWrite;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private DetailInfoMapFragment detailInfoMapFragment;
    private Bundle bundle;

    /* Firebase */
    private FirebaseFirestore firestore= FirebaseFirestore.getInstance(); //FirebaseFirestore
    private CollectionReference eventsReference;//firestore - events 참조
    private CollectionReference reviewsReference;//firestore - reviews 참조
    private CollectionReference bookmarksReference;//firestore - bookmarks 참조

    /* Firebase - user*/
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    //행사 정보
    private MarkerInfo markerInfo;
    private String contentid;
    private String title="";
    private String homepageUrl;
    private double latY,longX;

    //리뷰 목록
    private RecyclerView recycler_review;
    private ReviewInfo reviewInfo;
    private ArrayList<ReviewInfo> reviewsList = new ArrayList<ReviewInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_info);

        /* Firebase - CollectionReference */
        eventsReference = firestore.collection("events");
        reviewsReference= firestore.collection("reviews");
        bookmarksReference = firestore.collection("users").document(user.getUid()).collection("bookmarks");

        /* Bundle - Map Fragment에 좌표 전달 */
        bundle = new Bundle();

        /* contentid 가져오기 */
        Intent intent = getIntent();
        contentid = intent.getStringExtra("contentid");
        Log.e("실행","DetailInfoActivity:contentid-" + contentid);

        /* 위젯 가져오기 */
        ImageView imgvImage = (ImageView)findViewById(R.id.imgvImage);
        TextView tvTitle = (TextView)findViewById(R.id.tvTitle);
        TextView tvOverview = (TextView)findViewById(R.id.tvOverview);
        TextView tvInfoContents1 = (TextView)findViewById(R.id.tvInfoContents1);
        TextView tvInfoHompage = (TextView)findViewById(R.id.tvInfoHompage); //홈페이지 주소
        tvInfoHompage.setOnClickListener(onClickListener); //홈페이지 주소 이동
        TextView tvInfoContents2 = (TextView)findViewById(R.id.tvInfoContents2);
        Button btnAroundInfo = (Button)findViewById(R.id.btnAroundInfo);
        btnAroundInfo.setOnClickListener(onClickListener);
        TextView tvMapContents = (TextView)findViewById(R.id.tvMapContents);
        recycler_review = (RecyclerView)findViewById(R.id.recycler_review); //리뷰목록
        btnBook = (CheckBox)findViewById(R.id.btnBook); //북마크 버튼
        btnBook.setOnClickListener(onClickListener);
        /* bookmark 여부 확인 */
        if (user != null) {
            Query collquery = bookmarksReference.whereEqualTo("contentid", contentid);
            collquery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //북마크에 이미 있음 : btnBook.checked=true
                            btnBook.setChecked(true);
                            Log.e(TAG,"북마크에 이미 있음");
                        }
                    } else {
                        //북마크에 없음 : btnBook.checked=false
                        btnBook.setChecked(false);
                        Log.e(TAG,"북마크에 없음");
                    }
                }
            });
        }
        else {
            //모든 화면 닫고, 로그인 화면으로 이동
        }
        btnRvWrite = (TextView)findViewById(R.id.btnRvWrite);//리뷰 작성 버튼
        btnRvWrite.setOnClickListener(onClickListener);

        /* reviews 가져오기 */
        reviewsList.clear();
        getReviewList();

        /* contentid 문서 가져오기 */
        eventsReference.whereEqualTo("contentid", contentid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.e(TAG, "query.get(): " + task.isSuccessful());
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                /* contentid 필드 값 set */
                                //대표이미지
                                String firstimage = document.getData().get("firstimage").toString();
                                Glide.with(imgvImage).load(firstimage).override(imgvImage.getWidth(), imgvImage.getHeight()).into(imgvImage);
                                Log.e(TAG,"running-" + document.getData().get("running").toString());
                                if(document.getData().get("running").toString().equals("N")){
                                    TextView imgvText = (TextView)findViewById(R.id.imgvText);
                                    imgvText.setVisibility(View.VISIBLE);
                                }

                                //제목 및 개요
                                title = document.getData().get("title").toString();
                                tvTitle.setText(title);

                                tvOverview.setText(Html.fromHtml(document.getData().get("overview").toString()));

                                //소개정보
                                String agelimit = document.getData().get("agelimit").toString(); //관람 가능연령
                                tvInfoContents1.setText("[ 관람 가능연령 ]\n " + agelimit);

                                String eventstartdate = document.getData().get("eventstartdate").toString(); // 시작일
                                String eventenddate = document.getData().get("eventenddate").toString(); // 종료일
                                String playtime = document.getData().get("playtime").toString(); //공연시간
                                String program = document.getData().get("program").toString(); //행사 프로그램
                                String subevent = document.getData().get("subevent").toString(); //부대행사
                                tvInfoContents1.append("\n\n[ 기 간 ]\n " + eventstartdate + " ~ " + eventenddate + "\n");
                                tvInfoContents1.append("\n[ 공연시간 ]\n " + Html.fromHtml(playtime) + "\n");
                                tvInfoContents1.append("\n[ 행사 프로그램 ]\n " + Html.fromHtml(program) + "\n");
                                tvInfoContents1.append("\n[ 부대행사 ]\n " + Html.fromHtml(subevent));

                                String usetimefestival = document.getData().get("usetimefestival").toString(); //이용요금
                                String discountinfofestival = document.getData().get("discountinfofestival").toString(); //할인정보
                                String bookingplace = document.getData().get("bookingplace").toString(); //예매처
                                String homepage = document.getData().get("homepage").toString(); //홈페이지
                                tvInfoContents1.append("\n\n[ 이용요금 ]\n " + Html.fromHtml(usetimefestival) + "\n");
                                tvInfoContents1.append("\n[ 할인정보 ]\n " + Html.fromHtml(discountinfofestival) + "\n");
                                tvInfoContents1.append("\n[ 예매처 ]\n " + Html.fromHtml(bookingplace) + "\n");
                                tvInfoContents1.append("\n[ 홈페이지 ]\n ");
                                tvInfoHompage.setText(Html.fromHtml(homepage));
                                homepageUrl = Html.fromHtml(homepage).toString();

                                String sponsor1 = document.getData().get("sponsor1").toString(); //주최자
                                String sponsor2 = document.getData().get("sponsor2").toString(); //주관사
                                tvInfoContents2.setText("\n\n[ 주최자 ]\n " + Html.fromHtml(sponsor1)+ "\n");
                                tvInfoContents2.append("\n[ 주관사 ]\n " + Html.fromHtml(sponsor2));

                                //위치정보
                                String addr1 = document.getData().get("addr1").toString(); //주소
                                String addr2 = document.getData().get("addr2").toString(); //상세주소
                                String eventplace = document.getData().get("eventplace").toString(); //행사 장소
                                String placeinfo = document.getData().get("placeinfo").toString(); //행사 위치 안내
                                tvMapContents.setText("\n[ 주소 ]\n " + addr1 + " " + addr2+ "\n");
                                tvMapContents.append("\n[ 행사 장소 ]\n " + Html.fromHtml(eventplace) + "\n");
                                tvMapContents.append("\n[ 행사 위치 안내 ]\n " + Html.fromHtml(placeinfo));

                                /* MarkerInfo - 북마크용 */
                                markerInfo = new MarkerInfo(contentid, firstimage, title, eventplace);

                                /* Bundle */
                                bundle.putString("contentid", contentid);
                                latY=Double.parseDouble(document.getData().get("mapy").toString());
                                bundle.putDouble("latY", latY);
                                longX = Double.parseDouble(document.getData().get("mapx").toString());
                                bundle.putDouble("longX", longX);
                                Log.e(TAG,"query-mapy"+document.getData().get("mapy").toString());
                                Log.e(TAG,"query-mapx"+document.getData().get("mapx").toString());

                                Log.e(TAG,"query success");
                                detailInfoMapFragment = new DetailInfoMapFragment();
                                detailInfoMapFragment.setArguments(bundle);

                                fragmentManager = getSupportFragmentManager();
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.fragmentdetailmap, detailInfoMapFragment);

                                fragmentTransaction.commit();
                                Log.e(TAG,"commit()");
                            }
                        } else {
                            Log.e(TAG,"query failed");
                        }
                    }
        });


    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnBook:
                    //북마크 여부 확인 - checkbox:true||false
                    //파베에 북마크 정보 추가||삭제
                    if (btnBook.isChecked()) {
                        // TODO : CheckBox is checked.
                        bookmarksReference.add(markerInfo)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        //Upload성공
                                        Log.e(TAG, "EventInfo Upload Success.");
                                        Toastmsg("북마크가 저장되었습니다.");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Upload실패
                                    }
                                });
                        Log.e(TAG, "행사 저장 [" +  contentid + "]");
                    } else {
                        // TODO : CheckBox is unchecked.
                        Query query = bookmarksReference.whereEqualTo("contentid", contentid);
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String docid = document.getId();
                                        bookmarksReference.document(docid).delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot successfully deleted! >> " + docid);
                                                        Log.e(TAG, "북마크 삭제 [" +  contentid + "]");
                                                        Toastmsg("북마크가 삭제되었습니다.");
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
                            }
                        });
                    }
                    break;
                case R.id.btnRvWrite:
                    Intent intent = new Intent(getApplication(), ReviewWriteActivity.class);
                    intent.putExtra("contentid",contentid);
                    intent.putExtra("title",title);
                    startActivity(intent);
                    break;
                case R.id.tvInfoHompage:
                    Intent intenturl = new Intent(Intent.ACTION_VIEW);
                    intenturl.setData(Uri.parse(homepageUrl));
                    startActivity(intenturl);
                    break;
                case R.id.btnAroundInfo:
                    Log.e(TAG,"btnAroundInfo");
                    //Map Clear
                    Intent intentAround = new Intent(getApplication(), AroundInfoActivity.class);
                    intentAround.putExtra("latY",latY);
                    intentAround.putExtra("longX",longX);
                    startActivity(intentAround);
                    break;
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        //Map Clear
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(detailInfoMapFragment);
        fragmentTransaction.commit();

        reviewsList.clear();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fragmentTransaction = fragmentManager.beginTransaction();
        detailInfoMapFragment.setArguments(bundle);
        Log.e("실행", "MainActivity:mapFragment.setArguments(bundle)");
        fragmentTransaction.add(R.id.fragmentdetailmap, detailInfoMapFragment);
        Log.e("실행", "MainActivity:add()");
        fragmentTransaction.commitAllowingStateLoss();
        Log.e("실행", "MainActivity:commit()");

        /* reviews 가져오기 */
        getReviewList();
    }

    //리뷰 리스트 정렬
    Comparator<ReviewInfo> cmpAsc = new Comparator<ReviewInfo>() {
        @Override
        public int compare(ReviewInfo o1, ReviewInfo o2) {
            return o1.compareTo(o2) ;
        }
    };

    private void getReviewList(){
        reviewsReference.whereEqualTo("contentid", contentid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.e(TAG, "query.get(): " + task.isSuccessful());
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String reviewid = document.getId(); //Review Doc Id
                                Log.e(TAG, "reviewid: " + reviewid);
                                String writedate = document.getData().get("writedate").toString();
                                //String title = document.getData().get("title").toString(); => wrtiername
                                String writer = document.getData().get("writer").toString();
                                String contents = document.getData().get("contents").toString();
                                double rating = (double) document.getData().get("rating");
                                reviewInfo = new ReviewInfo(reviewid, writedate,writer,contents,rating);
                                reviewsList.add(reviewInfo);
                            }
                            if(reviewsList.size()!=0){
                                Collections.sort(reviewsList, cmpAsc); //sort
                                Log.e(TAG, "reviewsList: " + reviewsList.get(0).getReviewid());
                                recycler_review.setLayoutManager(new LinearLayoutManager(DetailInfoActivity.this));
                                RecyclerView.Adapter mAdapter = new DetailReviewsAdapter(DetailInfoActivity.this, reviewsList);
                                recycler_review.setAdapter(mAdapter);
                            }

                        } else {
                            Log.e(TAG,"query failed");
                        }
                    }
                });
    }
}