package com.comp90018.comp90018.ui.map;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.comp90018.comp90018.R;
import com.comp90018.comp90018.adapter.TripAdapter;
import com.comp90018.comp90018.model.DayPlan;
import com.comp90018.comp90018.model.Journey;
import com.comp90018.comp90018.model.Navigation;
import com.comp90018.comp90018.model.NavigationStep;
import com.comp90018.comp90018.model.TotalPlan;
import com.comp90018.comp90018.service.FirebaseService;
import com.comp90018.comp90018.service.GPTService;
import com.comp90018.comp90018.service.LocationService;
import com.comp90018.comp90018.service.NavigationService;
import com.comp90018.comp90018.service.StepCountService;
import com.comp90018.comp90018.ui.DialogFragment.LocationInputDialogFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
public class MapFragment extends Fragment {
    private MapView mapView;
    private GoogleMap googleMap;
    private LocationService locationService;
    private Marker currentLocationMarker;
    private NavigationService navigationService;
    private MapViewModel mapViewModel;
    private FloatingActionButton fabButton1;
    private FloatingActionButton fabButton2;
    private FloatingActionButton fabButton3;
    private NavController navController;
    private TextView textView;
    private TextView stepCountTextView;
    private CompletableFuture<String> firebaseFuture;
    private Polyline currentPolyline;

    private ArrayList<Journey> journeys = new ArrayList<>(); // 地理位置列表
    // private HashMap<Marker, String> markerInfoMap = new HashMap<>(); // 保存每个Marker对应的信息
    GPTService gptService = GPTService.getInstance();

    private BroadcastReceiver stepCountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.comp90018.STEP_COUNT_UPDATED")) {
                int stepCount = intent.getIntExtra("stepCount", 0);
                // Update the TextView with the step count
                stepCountTextView.setText("Steps: " + stepCount);
            }
        }
    };

    // 新添加的成员变量
    private LinearLayout floatingInfoWindow;
    private TextView placeNameTextView;
    private TextView visitDurationTextView;
    private Button navigationButton;
    private Button detailButton;

    private Journey selectedJourney; // 当前选中的景点

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        navigationService = new NavigationService();

        firebaseFuture = new CompletableFuture<>();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 当前用户ID
        FirebaseService.getInstance().getAllTotalPlans(userId,
                totalPlans -> {
                    // 存储唯一的 Journey 对象
                    Set<Journey> journeySet = new HashSet<>();

                    // 获取当前日期
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String today = dateFormat.format(Calendar.getInstance().getTime());
                    Log.d("MapFragment", String.valueOf(totalPlans.size()));
                    // 遍历所有的 TotalPlan
                    for (TotalPlan totalPlan : totalPlans) {
                        // 遍历 TotalPlan 中的所有 DayPlan
                        for (DayPlan dayPlan : totalPlan.getDayPlans()) {
                            // 检查 DayPlan 的日期是否为今天
                            if (dayPlan.getDate() != null && dateFormat.format(dayPlan.getDate()).equals(today)) {
                                // 将今天的 Journey 加入 Set 来去除重复
                                journeySet.addAll(dayPlan.getJourneys());
                            }
                        }
                    }
                    Log.d("MapFragment", String.valueOf(journeySet.size()));
                    journeys.addAll(journeySet);
                    journeys.add(new Journey("Flinders station",
                            "I hope play around this station about 2 hours", -37.8136, 144.9631)); // 墨尔本
                    firebaseFuture.complete("success");
                },
                e -> {
                    Log.w("PlanService", "Error getting documents", e);
                    firebaseFuture.completeExceptionally(new RuntimeException("fail to get info from firebase"));
                });
        textView = rootView.findViewById(R.id.textView_date);
        stepCountTextView = rootView.findViewById(R.id.textView_step_count);

        // 获取当前设备时间
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

        // 设置TextView的文本为当前日期时间
        textView.setText(currentDate);

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
                // 加载并应用自定义地图样式
                try {
                    boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                            getContext(), R.raw.map_style));

                    if (!success) {
                        Log.e("MapFragment", "Failed to apply map style");
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e("MapFragment", "Can't find map style. Error: ", e);
                }
                // 开始更新位置
                updateLocationOnMap();
                firebaseFuture.thenAccept(result -> {
                    addLocationsToMap();
                }).exceptionally(throwable -> {
                    Log.e("MapFragment", throwable.getMessage());
                    return null;
                });
                // 设置点击 Marker 弹出信息窗口
                // googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                //     @Override
                //     public boolean onMarkerClick(Marker marker) {
                //         // 弹出信息窗口
                //         String info = markerInfoMap.get(marker);
                //         if (info != null) {
                //             showInfoWindow(marker, info);
                //         }
                //         return false;  // 返回 false 以继续显示默认的 InfoWindow
                //     }
                // });

                // 修改后的 Marker 点击事件
                googleMap.setOnMarkerClickListener(marker -> {
                    Journey journey = (Journey) marker.getTag();
                    if (journey != null) {
                        selectedJourney = journey;

                        // 设置悬浮窗内容
                        placeNameTextView.setText(journey.getName());
                        visitDurationTextView.setText("Approximate Visit Time: " + journey.getNotes());

                        // 显示悬浮窗
                        floatingInfoWindow.setVisibility(View.VISIBLE);
                    }
                    return true; // 返回 true 表示消费了点击事件
                });

                // 当点击地图其他地方时，隐藏悬浮窗
                googleMap.setOnMapClickListener(latLng -> {
                    floatingInfoWindow.setVisibility(View.GONE);
                });

            }
        });

        // 初始化位置服务
        locationService = new LocationService(requireContext());
        Log.d("MapFragment", "LocationService initialized");

        // 初始化悬浮窗组件
        floatingInfoWindow = rootView.findViewById(R.id.floating_info_window);
        placeNameTextView = rootView.findViewById(R.id.place_name);
        visitDurationTextView = rootView.findViewById(R.id.visit_duration);
        navigationButton = rootView.findViewById(R.id.button_navigation);
        detailButton = rootView.findViewById(R.id.button_detail);

        // 设置按钮点击事件
        navigationButton.setOnClickListener(v -> {
            if (selectedJourney != null) {
                displayRouteFromCurrentLocation(selectedJourney);
            }
        });

        detailButton.setOnClickListener(v -> {
            if (selectedJourney != null) {
                showDetailInfo(selectedJourney);
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Start the step count service
        Intent intent = new Intent(requireContext(), StepCountService.class);
        requireContext().startService(intent);

        navController = androidx.navigation.Navigation.findNavController(requireView());

        fabButton1 = view.findViewById(R.id.fab_button1);
        fabButton2 = view.findViewById(R.id.fab_button2);
        fabButton3 = view.findViewById(R.id.fab_button3);

        // 调整按钮点击事件，确保逻辑与原始按钮匹配
        fabButton1.setOnClickListener(view1 -> navController.navigate(R.id.action_map_to_camera));

        // 修改 fabButton2 的点击事件，打开输入对话框
        fabButton2.setOnClickListener(v -> {
            LocationInputDialogFragment dialog = new LocationInputDialogFragment();

            // 设置数据回调监听器
            dialog.setOnLocationSetListener((startLocation, endLocation) -> {
                // 更新UI，显示用户输入的起点和终点
                displayRoute(startLocation, endLocation);
            });

            // 显示对话框
            dialog.show(getChildFragmentManager(), "LocationInputDialog");
        });

        fabButton3.setOnClickListener(v -> {
            // 可以添加与第三个按钮相关的逻辑
            Toast.makeText(getContext(), "Button 3 clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void displayRoute(String origin, String destination) {
        // 清除上一次的路线
        if (currentPolyline != null) {
            currentPolyline.remove();
        }

        navigationService.getDirections(origin, destination, new NavigationService.DirectionsCallback() {
            @Override
            public void onSuccess(Navigation routes) {
                getActivity().runOnUiThread(() -> {
                    PolylineOptions polylineOptions = new PolylineOptions()
                            .color(Color.BLUE) // 设置线的颜色，例如蓝色
                            .width(10); // 设置线的宽度，例如10像素;
                    for (NavigationStep route : routes.getNavigationSteps()) {
                        for (LatLng point : route.getRoutes()) {
                            polylineOptions.add(point);
                        }
                    }
                    currentPolyline = googleMap.addPolyline(polylineOptions);
                });
            }

            @Override
            public void onFailure(Exception e) {
                // 处理错误
                Log.e("MapFragment", "Failed to get directions", e);
            }
        });
    }


    private void displayRouteFromCurrentLocation(Journey journey) {
        if (currentLocationMarker == null) {
            Toast.makeText(getContext(), "Current location not available", Toast.LENGTH_SHORT).show();
            return;
        }

        LatLng currentLocation = currentLocationMarker.getPosition();
        LatLng destination = new LatLng(journey.getLatitude(), journey.getLongitude());

        // 清除上一次的路线
        if (currentPolyline != null) {
            currentPolyline.remove();
        }

        String origin = currentLocation.latitude + "," + currentLocation.longitude;
        String dest = destination.latitude + "," + destination.longitude;

        navigationService.getDirections(origin, dest, new NavigationService.DirectionsCallback() {
            @Override
            public void onSuccess(Navigation routes) {
                getActivity().runOnUiThread(() -> {
                    PolylineOptions polylineOptions = new PolylineOptions()
                            .color(Color.BLUE)
                            .width(10);
                    for (NavigationStep route : routes.getNavigationSteps()) {
                        for (LatLng point : route.getRoutes()) {
                            polylineOptions.add(point);
                        }
                    }
                    currentPolyline = googleMap.addPolyline(polylineOptions);
                });
            }

            @Override
            public void onFailure(Exception e) {
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
        Log.d("MapFragment", String.valueOf(journeys.size()));
        for (Journey journey : journeys) {
            // 创建局部变量，防止闭包问题
            final LatLng location = new LatLng(journey.getLatitude(), journey.getLongitude());
            final String name = journey.getName();
            final String notes = journey.getNotes();

            // 检查是否已缓存介绍
            if (mapViewModel.hasIntroduction(name)) {
                // 如果 ViewModel 已经缓存了介绍，则直接使用
                String cachedIntroduction = mapViewModel.getIntroduction(name);
                addMarkerWithInfo(location, journey, cachedIntroduction);
            } else {
                // 使用 GPTService 获取介绍
                gptService.getJourneyIntroduction(name, notes, new GPTService.GPTCallback() {
                    @Override
                    public void onSuccess(String result) {
                        mapViewModel.addIntroduction(name, result);  // 缓存结果到 ViewModel
                        addMarkerWithInfo(location, journey, result);
                        Log.d("GPTService", "request gpt api success");
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e("GPTService", "Failed to generate introduction: " + error);
                        // 或者显示一个 Toast 提示用户
                        Toast.makeText(requireContext(), "Failed to generate introduction", Toast.LENGTH_SHORT).show();
                        // 即使获取介绍失败，也添加 Marker
                        addMarkerWithInfo(location, journey, null);
                    }
                });
            }
        }
    }


    // 新增方法：创建自定义的 Marker 图标
    private Bitmap createCustomMarker(Journey journey) {
        View markerView = LayoutInflater.from(getContext()).inflate(R.layout.custom_marker_layout, null);

        // 设置名称
        TextView markerName = markerView.findViewById(R.id.marker_name);
        markerName.setText(journey.getName());

        // 设置图片
        ImageView markerImage = markerView.findViewById(R.id.marker_image);

        // TODO: 将来可以从数据库或网络获取 Journey 对象对应的图片
        // 目前，使用静态的统一图片进行展示
        markerImage.setImageResource(R.drawable.sample_image);

        // 测量并布局 View
        markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        markerView.layout(0, 0, markerView.getMeasuredWidth(), markerView.getMeasuredHeight());

        // 创建 Bitmap
        Bitmap bitmap = Bitmap.createBitmap(markerView.getMeasuredWidth(), markerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerView.draw(canvas);

        return bitmap;
    }

    private void addMarkerWithInfo(LatLng location, Journey journey, String info) {
        // 创建自定义的 Marker 图标
        BitmapDescriptor customIcon = BitmapDescriptorFactory.fromBitmap(createCustomMarker(journey));

        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(location)
                .title(journey.getName())
                .icon(customIcon));

        marker.setTag(journey); // 将 Journey 对象绑定到 Marker

        // 将介绍信息缓存到 MapViewModel 中
        if (info != null) {
            mapViewModel.addIntroduction(journey.getName(), info);
        }
    }


    /*
    // 原有方法，已注释
    private void addMarkerWithInfo(LatLng location, String name, String info) {
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(location)
                .title(name)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        markerInfoMap.put(marker, info);
    }
    */

    /*
    // 原有方法，已注释
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
    */

    // 新增方法：显示景点详细信息
    private void showDetailInfo(Journey journey) {
        String info = mapViewModel.getIntroduction(journey.getName());
        if (info != null) {
            // 创建一个自定义布局
            LayoutInflater inflater = LayoutInflater.from(requireContext());
            View dialogView = inflater.inflate(R.layout.custom_info_window, null);

            // 在自定义布局中设置文本内容
            TextView infoTextView = dialogView.findViewById(R.id.info_text);
            infoTextView.setText(info);

            // 创建 AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setView(dialogView);

            // 设置对话框标题
            builder.setTitle(journey.getName());

            // 添加关闭按钮
            builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());

            // 显示对话框
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            Toast.makeText(requireContext(), "No detail information available", Toast.LENGTH_SHORT).show();
        }
    }


    public void clearRoute() {
        if (currentPolyline != null) {
            currentPolyline.remove();
            currentPolyline = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter("com.comp90018.STEP_COUNT_UPDATED");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().registerReceiver(stepCountReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        }

        updateLocationOnMap(); // 启动位置更新
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();

        requireContext().unregisterReceiver(stepCountReceiver);

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