package com.comp90018.comp90018.ui.map;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.comp90018.comp90018.R;
import com.comp90018.comp90018.service.LocationService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.CameraPosition;

import java.util.ArrayList;
import java.util.HashMap;

public class MapFragment extends Fragment {
    private MapView mapView;
    private GoogleMap googleMap;
    private LocationService locationService;
    private Marker currentLocationMarker;

    private ArrayList<LatLng> locations = new ArrayList<>(); // 地理位置列表
    private ArrayList<String> infoList = new ArrayList<>();  // 对应的信息列表
    private HashMap<Marker, String> markerInfoMap = new HashMap<>(); // 保存每个Marker对应的信息

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        // 初始化Google地图
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
                // 设置地图默认位置为墨尔本
                LatLng defaultLocation = new LatLng(-37.8136, 144.9631);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));

                googleMap.getUiSettings().setZoomGesturesEnabled(true);  // 启用缩放手势
                googleMap.getUiSettings().setScrollGesturesEnabled(true);  // 启用拖动手势
                googleMap.getUiSettings().setRotateGesturesEnabled(true);  // 启用旋转手势
                googleMap.getUiSettings().setTiltGesturesEnabled(true);  // 启用倾斜手势

                Log.d("MapFragment", "Map loaded with default location");

                // 开始更新位置
                updateLocationOnMap();
                addLocationsToMap();
                // 设置点击 Marker 弹出信息窗口
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        // 弹出信息窗口
                        String info = markerInfoMap.get(marker);
                        if (info != null) {
                            showInfoWindow(marker, info);
                        }
                        return false;  // 返回 false 以继续显示默认的 InfoWindow
                    }
                });
            }
        });

        // 初始化位置服务
        locationService = new LocationService(requireContext());
        Log.d("MapFragment", "LocationService initialized");
        // 添加一些示例位置和信息
        locations.add(new LatLng(-37.8136, 144.9631)); // 墨尔本
        infoList.add("这是墨尔本的位置，显示一些额外信息...");

        locations.add(new LatLng(-37.9285, 144.1631)); // 阿德莱德
        infoList.add("这是阿德莱德的位置，更多详细信息可以在这里显示...");


        return rootView;
    }

    private void updateLocationOnMap() {
        Log.d("MapFragment", "Attempting to update location on map");

        locationService.startLocationUpdates(new LocationService.LocationCallback() {
            @Override
            public void onLocationResult(Location location) {
                if (location != null) {
                    // 获取经纬度
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Log.d("MapFragment", "Location found: Lat=" + latitude + ", Lon=" + longitude);

                    // 获取当前位置的LatLng
                    LatLng currentLocation = new LatLng(latitude, longitude);

                    // 如果存在旧的Marker，移除它
                    if (currentLocationMarker != null) {
                        currentLocationMarker.remove();
                    }

                    // 在地图上添加当前位置的标记
                    currentLocationMarker = googleMap.addMarker(new MarkerOptions()
                            .position(currentLocation)
                            .title("当前位置")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                    // 更新地图中心并设置缩放级别
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(currentLocation)   // 设置地图中心点
                            .zoom(15)                  // 设置缩放级别
                            .build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    Log.d("MapFragment", "Map centered to current location");
                }
            }

            @Override
            public void onLocationError(String errorMsg) {
                Log.e("MapFragment", "Error getting location: " + errorMsg);
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    // 将位置添加到地图上
    private void addLocationsToMap() {
        for (int i = 0; i < locations.size(); i++) {
            LatLng location = locations.get(i);
            String info = infoList.get(i);

            // 添加标记
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title("自定义位置")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));  // 使用不同的颜色图标

            // 保存 Marker 和 对应信息的映射关系
            markerInfoMap.put(marker, info);
        }
    }
    // 显示自定义的信息窗口
    private void showInfoWindow(Marker marker, String info) {
        // 显示 Toast 或者使用一个自定义的布局弹窗
        Toast.makeText(requireContext(), info, Toast.LENGTH_LONG).show();

        // 你也可以创建一个自定义的弹窗，显示更加丰富的信息
        // 示例中使用了Toast，你可以选择自定义布局来显示详细信息
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        updateLocationOnMap(); // 启动位置更新
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        locationService.stopLocationUpdates(); // 停止位置更新
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}