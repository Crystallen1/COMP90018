package com.comp90018.comp90018.service;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


public class LocationService {
    private static final String TAG = "LocationService";
    private LocationListener locationListener;
    private LocationManager locationManager;
    private Context context;

    public interface LocationCallback {
        void onLocationResult(Location location);
        void onLocationError(String errorMsg);
    }

    public LocationService(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Log.d(TAG, "LocationManager initialized");
    }
    // 注册位置更新监听器
    public void startLocationUpdates(LocationCallback callback) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 请求权限
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            callback.onLocationError("Location permission not granted");
            return;
        }

        // 定义LocationListener
        this.locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    // 位置发生变化时调用回调
                    callback.onLocationResult(location);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        // 请求位置更新，设置时间和距离的最小间隔
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
    }

    // 停止位置更新
    public void stopLocationUpdates() {
        if (locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
    public void getCurrentLocation(LocationCallback callback) {
        Log.d(TAG, "getCurrentLocation called"); // 方法调用日志

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 请求位置权限
            Log.w(TAG, "Location permission not granted, requesting permission"); // 权限问题日志
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            callback.onLocationError("Location permission not granted");
            return;
        }
        // 请求位置更新
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled) {
            // 提示用户启用位置服务
            Toast.makeText(context, "Please enable location services", Toast.LENGTH_LONG).show();
        }

        if (location != null) {
            Log.d(TAG, "Location found: Lat=" + location.getLatitude() + ", Lon=" + location.getLongitude()); // 输出位置信息
            callback.onLocationResult(location);
        } else {
            Log.e(TAG, "Unable to get current location"); // 错误日志
            callback.onLocationError("Unable to get current location");
        }
    }
}