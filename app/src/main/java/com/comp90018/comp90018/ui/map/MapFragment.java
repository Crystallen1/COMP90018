package com.comp90018.comp90018.ui.map;
import android.app.AlertDialog;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.comp90018.comp90018.R;
import com.comp90018.comp90018.model.Journey;
import com.comp90018.comp90018.service.GPTService;
import com.comp90018.comp90018.service.LocationService;
import com.comp90018.comp90018.service.NavigationService;
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
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapFragment extends Fragment {
    private MapView mapView;
    private GoogleMap googleMap;
    private LocationService locationService;
    private Marker currentLocationMarker;
    private NavigationService navigationService;

    private ArrayList<Journey> journeys = new ArrayList<>(); // 地理位置列表
    private HashMap<Marker, String> markerInfoMap = new HashMap<>(); // 保存每个Marker对应的信息

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        navigationService = new NavigationService(getString(R.string.google_api_key));


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
                displayRoute("Sydney", "Melbourne");
            }
        });

        // 初始化位置服务
        locationService = new LocationService(requireContext());
        Log.d("MapFragment", "LocationService initialized");
        // 添加一些示例位置和信息
        journeys.add(new Journey("Flinders station","I hope play around this station about 2 hours",-37.8136, 144.9631)); // 墨尔本

//        journeys.add(new Journey("place2","这是阿德莱德的位置，更多详细信息可以在这里显示...",-37.8285, 144.9641)); // 墨尔本


        return rootView;
    }

    private void displayRoute(String origin, String destination) {
        navigationService.getDirections(origin, destination, new NavigationService.DirectionsCallback() {
            @Override
            public void onSuccess(List<String> routes) {
                requireActivity().runOnUiThread(() -> {
                    // 在地图上绘制路线
                    for (String route : routes) {
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                // 处理错误
                Log.e("MapFragment", "Failed to get directions", e);
            }
        });
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
        GPTService gptService = new GPTService();

        for (int i = 0; i < journeys.size(); i++) {
            LatLng location = new LatLng(journeys.get(i).getLatitude(),journeys.get(i).getLongitude());
            String name = journeys.get(i).getName();
            String notes = journeys.get(i).getNotes();
            new FetchJourneyInfoTask(name, notes, location).execute();
//            String generatedInfo = gptService.getJourneyIntroduction(name, notes);
//
//
//            // 添加标记
//            Marker marker = googleMap.addMarker(new MarkerOptions()
//                    .position(location)
//                    .title(journeys.get(i).getName())
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));  // 使用不同的颜色图标
//
//            // 保存 Marker 和 对应信息的映射关系
//            markerInfoMap.put(marker, generatedInfo);
        }
    }
    private class FetchJourneyInfoTask extends AsyncTask<Void, Void, String> {
        private String name;
        private String notes;
        private LatLng location;

        public FetchJourneyInfoTask(String name, String notes, LatLng location) {
            this.name = name;
            this.notes = notes;
            this.location = location;
        }

        @Override
        protected String doInBackground(Void... voids) {
            GPTService gptService = new GPTService();
            return gptService.getJourneyIntroduction(name, notes);
        }

        @Override
        protected void onPostExecute(String generatedInfo) {
            // 在地图上添加标记并显示生成的介绍
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

            // 保存 Marker 和 生成的信息
            markerInfoMap.put(marker, generatedInfo);
        }
    }
    // 显示自定义的信息窗口
    private void showInfoWindow(Marker marker, String info) {
        // 创建一个自定义布局
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.custom_info_window, null);

        // 在自定义布局中设置文本内容
        TextView infoTextView = dialogView.findViewById(R.id.info_text);
        infoTextView.setText(info);

        // 创建 AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        // 设置对话框标题（可选）
        builder.setTitle("Place Information");

        // 添加关闭按钮（可选）
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());

        // 显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();
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