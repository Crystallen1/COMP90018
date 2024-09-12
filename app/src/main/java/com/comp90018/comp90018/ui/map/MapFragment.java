package com.comp90018.comp90018.ui.map;
import android.content.pm.PackageManager;
import android.graphics.Color;
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

import com.comp90018.comp90018.service.LocationService;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.comp90018.comp90018.R;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

public class MapFragment extends Fragment {

    private MapView mapView;
    private LocationService locationService;
    private GraphicsOverlay graphicsOverlay;  // 全局的GraphicsOverlay

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate布局
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = rootView.findViewById(R.id.mapView);

        // 加载地图
        ArcGISMap map = new ArcGISMap(Basemap.Type.STREETS, -37.8136, 144.9631, 12);
        mapView.setMap(map);
        Log.d("MapFragment", "Map loaded with default location");
        // 初始化GraphicsOverlay
        graphicsOverlay = new GraphicsOverlay();
        mapView.getGraphicsOverlays().add(graphicsOverlay);

//         初始化位置服务
        locationService = new LocationService(requireContext());
        Log.d("MapFragment", "LocationService initialized");

//
//         获取并显示当前位置
        updateLocationOnMap();

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

                    // 清除之前的图形（红点）
                    graphicsOverlay.getGraphics().clear();

                    // 在地图上添加当前位置的标记
                    Point currentLocation = new Point(longitude, latitude, SpatialReferences.getWgs84());
                    SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 10);
                    Graphic graphic = new Graphic(currentLocation, markerSymbol);
                    graphicsOverlay.getGraphics().add(graphic);

                    // 将地图居中到当前位置
                    mapView.setViewpointCenterAsync(currentLocation, 12);
                    mapView.setViewpoint(new Viewpoint(location.getLatitude(), location.getLongitude(), 30000));
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

//    private void updateLocationOnMap() {
//        Log.d("MapFragment", "Attempting to update location on map");
//
//        locationService.getCurrentLocation(new LocationService.LocationCallback() {
//            @Override
//            public void onLocationResult(Location location) {
//                if (location != null) {
//                    // 获取经纬度
//                    double latitude = location.getLatitude();
//                    double longitude = location.getLongitude();
//                    Log.d("MapFragment", "Location found: Lat=" + latitude + ", Lon=" + longitude);
//
//
//                    // 在地图上添加当前位置的标记
//                    Point currentLocation = new Point(longitude, latitude, SpatialReferences.getWgs84());
//                    GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
//                    mapView.getGraphicsOverlays().add(graphicsOverlay);
//
//                    SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 10);
//                    Graphic graphic = new Graphic(currentLocation, markerSymbol);
//                    graphicsOverlay.getGraphics().add(graphic);
//
//                    // 将地图居中到当前位置
//                    mapView.setViewpointCenterAsync(currentLocation, 12);
//                    mapView.setViewpoint(new Viewpoint(location.getLatitude(), location.getLongitude(), 30000));
//                    Log.d("MapFragment", "Map centered to current location");
//
//                }
//            }
//
//            @Override
//            public void onLocationError(String errorMsg) {
//                Log.e("MapFragment", "Error getting location: " + errorMsg);
//                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.resume();
        }
        updateLocationOnMap(); // 启动位置更新
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.pause();
        }
        locationService.stopLocationUpdates(); // 停止位置更新
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapView != null) {
            mapView.dispose();
        }
    }
}