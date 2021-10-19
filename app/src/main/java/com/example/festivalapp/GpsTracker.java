package com.example.festivalapp;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class GpsTracker extends Service implements LocationListener {
    private final Context mContext; //1.GpsTracker 객체 생성
    Location location;
    double latitude=37.568477;  //latitude-위도 -y좌표 -37
    double longitude=126.981611; //longitude-경도 -x좌표 -126

    protected LocationManager locationManager;
    /* gps 위치 업데이트 */
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    //2.생성자
    public GpsTracker(Context mContext) {
        this.mContext = mContext;
        //3.getLocation 메소드를 실행
        getLocation();
    }

    //getter - latitude
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }
    //getter - longitude
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    //3.getLocation()
    public Location getLocation() {
        try {
            //4.locationManger를 통해서 GPS와 네트워크가 사용가능한지 체크
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {

            } else { //GPS와 네트워크가 사용 가능할 때
                //5.접근할 수 있는 권한 획득
                int hasFineLocationPermission = ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

                if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                        hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

                } else{
                    return null;
                }

                if (isNetworkEnabled) { //네트워크 권한
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                if (isGPSEnabled) { //GPS권한
                   if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            Log.d("@@@", ""+e.toString());
        }

        return location;
    }

    @Nullable
    @Override //extends Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override //implements LocationListener
    public void onLocationChanged(@NonNull Location location) {

    }

    public void stopUsingGPS() { if(locationManager != null) { locationManager.removeUpdates(GpsTracker.this); } }
}
