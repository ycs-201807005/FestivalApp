package com.example.festivalapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.festivalapp.activity.ConfigActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;

public class GPSActivity extends ConfigActivity {
    private static final String TAG = "GPSActivity";
    private GpsTracker gpsTracker;
    /*
    * latitude-위도 -y좌표 :37
    * longitude-경도 -x좌표 :126
    * */
    private double latitude,longitude;
    //주소
    private List<Address> address=null;
    private String addr;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);
        Log.e(TAG,"onCreate()");

        if (checkLocationServicesStatus()) { //1.권한에 접근할 수 있는지 여부를 확인
            checkRunTimePermission(); //2.위치 퍼미션을 가지고 있지 않으면 퍼미션 요청을 하는 메소드
        } else {
            showDialogForLocationServiceSetting(); //3. 권한을 설정해주기 위한 다이얼로그창을 띄워주는 메소드 - [설정]앱으로 이동??
        }
    }

    //2.locationManager 객체를 사용하여 권한에 접근할 수 있는지 여부를 확인
    public boolean checkLocationServicesStatus() {
        Log.e(TAG, "checkLocationServicesStatus() 실행 - 권한에 접근할 수 있는지 여부를 확인");
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    //ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);

        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;
            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                //★위치 값을 가져올 수 있음!!
                try {
                    /* 현재 사용자 위치 좌표 */
                    //gpsTracker 객체를 생성하고 getLatitude와 getLongitude 메소드를 사용하여 latitude와 longitude에 위도와 경도를 설정
                    gpsTracker = new GpsTracker(GPSActivity.this); // GpsTracker : getLocation()
                    latitude = gpsTracker.getLatitude(); //y 위도
                    longitude = gpsTracker.getLongitude(); //x 경도
                    Log.e(TAG, "[현재위치] 위도(y)= " + latitude + ", 경도(x)= " + longitude);
                    /* 현재 사용자 위치 주소 */
                    Geocoder g = new Geocoder(this);
                    address = g.getFromLocation(latitude,longitude,10);
                    addr = address.get(0).getAddressLine(0);
                    Log.e(TAG, "[현재위치] 주소 = " + addr);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG,"입출력오류");
                }
                if(address!=null){
                    if(address.size()==0){
                        Toastmsg("주소찾기 오류");
                    }else{
                        showDialogForLocationSave(); //사용자 위치 확인 후 저장
                    }
                }

            } else {
                Log.e(TAG,"onRequestPermissionsResult() - 권한 거부");
                /* 기본 위치 좌표 */
                latitude = 37.566741959771605; //-위도 -y좌표
                Log.e(TAG,"latitude(X,위도) - " + latitude);
                longitude = 126.97787155945724; //-경도 -x좌표
                Log.e(TAG,"longitude(y, 경도) - " + longitude);
                /* 기본 위치 주소 */
                addr = "서울특별시 중구 명동 세종대로 110 : 서울특별시청";
                Log.e(TAG,"addr - " + addr);
                //사용자 위치 확인 후 저장
                showDialogForDefaultLocationSave();
            }
        }
    }

    //2.런타임 퍼미션 처리 : 위치 퍼미션을 가지고 있지 않으면 퍼미션 요청을 하는 메소드
    void checkRunTimePermission() {
        Log.e(TAG, "checkRunTimePermission() 실행 - 위치 퍼미션을 가지고 있지 않으면 퍼미션 요청을 하는 메소드");
        // (1) 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(GPSActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(GPSActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            //★(2) 이미 퍼미션을 가지고 있다면, 위치 값을 가져올 수 있음!!
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
            Log.e(TAG, "checkRunTimePermission() 실행 - (2) 이미 퍼미션을 가지고 있다면, 위치 값을 가져올 수 있음!!");
            try {
                /* 현재 사용자 위치 좌표 */
                //gpsTracker 객체를 생성하고 getLatitude와 getLongitude 메소드를 사용하여 latitude와 longitude에 위도와 경도를 설정
                gpsTracker = new GpsTracker(GPSActivity.this); // GpsTracker : getLocation()
                Log.e(TAG, "checkRunTimePermission() 실행 - GpsTracker 객체 생성");
                latitude = gpsTracker.getLatitude(); //x 경도
                Log.e(TAG, "checkRunTimePermission() 실행 - latitude(X,위도) = " + latitude);
                longitude = gpsTracker.getLongitude(); //y 위도
                Log.e(TAG, "checkRunTimePermission() 실행 - longitude(y, 경도) = " + longitude);
                Log.e(TAG, "[현재위치] 위도X= " + latitude + ", 경도y= " + longitude);
                /* 현재 사용자 위치 주소 */
                Geocoder g = new Geocoder(this);
                address = g.getFromLocation(latitude,longitude,10); //(y,x)
                addr = address.get(0).getAddressLine(0);
                Log.e(TAG, "[현재위치] 주소 = " + addr);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG,"입출력오류");
            }
            if(address!=null){
                if(address.size()==0){
                    Toastmsg("주소찾기 오류");
                }else{
                    showDialogForLocationSave(); //사용자 위치 확인 후 저장
                }
            }
        } else {
            //(2') 퍼미션 요청을 허용한 적이 없다면, 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
            // (3-1) 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(GPSActivity.this, REQUIRED_PERMISSIONS[0])) {
                // (3-2) 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(GPSActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // ★(3-3) 사용자에게 퍼미션 요청을 합니다.
                // 요청 결과는 "onRequestPermissionResult()"에서 수신됩니다.
                ActivityCompat.requestPermissions(GPSActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
                Log.e(TAG, "checkRunTimePermission() 실행 - ★(3-3) 사용자에게 퍼미션 요청을 합니다.");
            } else {
                // ★(4-1) 사용자가 퍼미션 거부를 한 적이 없는 경우, 퍼미션 요청을 바로 합니다.
                // 요청 결과는 "onRequestPermissionResult()"에서 수신됩니다.
                ActivityCompat.requestPermissions(GPSActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
                Log.e(TAG, "checkRunTimePermission() 실행 - ★(4-1) 사용자가 퍼미션 거부를 한 적이 없는 경우, 퍼미션 요청을 바로 합니다.");
            }
        }
    }

    //3.권한 설정 - 권한을 설정해주기 위한 다이얼로그창을 띄워주는 메소드
    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GPSActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    /* 권한 허용 시 - 사용자 위치 확인 다이얼로그 창 */
    private void showDialogForLocationSave() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GPSActivity.this);
        builder.setTitle("현재 위치 정보");
        builder.setMessage("현재 위치가 설정됩니다.\n현재 주소 : " + addr);
        builder.setCancelable(true);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Log.e(TAG,"showDialogForLocationSave() - 확인");
                /* 현재 사용자 위치 저장 */
                saveLocation();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                //1-이전 정보 사용
                //2-기본 위치 사용
                /* Firebase : Firestore */
                FirebaseFirestore firestore = FirebaseFirestore.getInstance(); //FirebaseFirestore
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //FirebaseUser
                DocumentReference docRef = firestore.collection("users").document(user.getUid());
                docRef.get().addOnCompleteListener((task) -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                if(document.get("mapx")==""){
                                    Log.e(TAG,"showDialogForLocationSave() - 취소");
                                    /* 기본 위치 좌표 */
                                    latitude = 37.568477; //-위도 -y좌표
                                    Log.e("GPSActtivity","latitude(y,위도) - " + latitude);
                                    longitude = 126.981611; //-경도 -x좌표
                                    Log.e("GPSActtivity","longitude(x, 경도) - " + longitude);
                                    /* 기본 위치 주소 */
                                    addr = "기본 위치 주소";
                                    Log.e("GPSActtivity","addr - " + addr);
                                    showDialogForDefaultLocationSave(); //사용자 위치 확인 후 저장
                                }
                                else {
                                    Toastmsg("이전 위치 사용");
                                    onStartActivity(ContentIdListActivity.class);
                                    finish();
                                }
                            } else {

                            }
                        }
                    } else {

                    }
                });
            }
        });
        builder.create().show();
    }

    /* 권한 거부 시 - 기본 위치 확인 다이얼로그 창 */
    private void showDialogForDefaultLocationSave() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GPSActivity.this);
        builder.setTitle("기본 위치 정보");
        builder.setMessage("※퍼미션이 거부되었습니다.※\n현재 위치가 기본 주소로 설정됩니다.\n기본 주소 : " + addr);
        builder.setCancelable(true);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                /* 현재 사용자 위치 저장 */
                saveLocation();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                // 거부한 퍼미션이 있다면, 앱을 사용할 수 없는 이유를 설명 - 2가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(GPSActivity.this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(GPSActivity.this, REQUIRED_PERMISSIONS[1])) {
                    Toast.makeText(GPSActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    //finish();

                } else {
                    Toast.makeText(GPSActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.create().show();
    }

    /* 사용자 현재 위치 정보 Firebase에 저장 */
    private void  saveLocation(){
        /* Firebase : Firestore */
        FirebaseFirestore firestore = FirebaseFirestore.getInstance(); //FirebaseFirestore
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //FirebaseUser
        Log.e(TAG,"user ="+user.getUid());
        try {
            firestore.collection("users").document(user.getUid()).update("mapx",longitude) //-경도 -x좌표 :126
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e(TAG,"Location:mapx Update Success.-" + longitude);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG,"Location:mapx Update Fail.");
                            //finish();
                        }
                    });
            firestore.collection("users").document(user.getUid()).update("mapy",latitude) //-위도 -y좌표 :37
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e(TAG,"Location:mapy Update Success.-"+latitude);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG,"Location:mapy Update Fail.");
                            //finish();
                        }
                    });
            firestore.collection("users").document(user.getUid()).update("address",addr)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e(TAG,"Location:address Update Success.-"+addr);
                            Toastmsg("위치가 저장되었습니다.");
                            onStartActivity(ContentIdListActivity.class);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG,"Location:address Update Fail.");
                            //finish();
                        }
                    });
            
            
            //finish();
        }
        catch (Exception e){
            Toastmsg("위치 저장 실패");

        }
    }

}
