package com.comp90018.comp90018.service;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import androidx.core.app.ActivityCompat;

public class LocationService {

    private LocationManager locationManager;

    public LocationService(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public Location getCurrentLocation(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 请求权限或提示用户需要权限
            return null;
        }

        // 获取GPS的最后已知位置
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    // 添加更多的位置信息处理逻辑，例如监听位置变化
}