package com.comp90018.comp90018.service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.internal.PolylineEncoding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NavigationService {

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/directions/json";
    private String apiKey;

    public NavigationService(String apiKey) {
        this.apiKey = apiKey;
    }

    public void getDirections(String origin, String destination, DirectionsCallback callback) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL).newBuilder();
        urlBuilder.addQueryParameter("origin", origin);
        urlBuilder.addQueryParameter("destination", destination);
        urlBuilder.addQueryParameter("key", apiKey);

        String url = urlBuilder.build().toString();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();

                    // 解析JSON并将数据传递给回调
                    callback.onSuccess(parseDirectionsResponse(jsonResponse));
                } else {
                    callback.onFailure(new IOException("Unexpected code " + response));
                }
            }
        });
    }

    private List<LatLng> parseDirectionsResponse(String jsonResponse) {
        // 解析JSON并返回Route列表
        // 可使用Gson或其他JSON解析库进行解析
        // 返回Route对象或Polyline数据
//        Log.d("NavigationService",jsonResponse);
        // 解析JSON
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);

            // 获取routes
            JSONArray routes = jsonObject.getJSONArray("routes");
            if (routes.length() > 0) {
                JSONObject route = routes.getJSONObject(0);  // 只取第一条路线
                JSONArray legs = route.getJSONArray("legs");
                List<LatLng> result = new ArrayList<>();
                for (int i = 0; i < legs.length(); i++) {
                    JSONObject leg = legs.getJSONObject(i);

                    // 获取距离和持续时间
                    String distance = leg.getJSONObject("distance").getString("text");
                    String duration = leg.getJSONObject("duration").getString("text");
                    String startAddress = leg.getString("start_address");
                    String endAddress = leg.getString("end_address");

                    System.out.println("起点: " + startAddress);
                    System.out.println("终点: " + endAddress);
                    System.out.println("距离: " + distance);
                    System.out.println("时长: " + duration);

                    // 获取每一步的导航详情
                    JSONArray steps = leg.getJSONArray("steps");
                    for (int j = 0; j < steps.length(); j++) {
                        JSONObject step = steps.getJSONObject(j);

                        String instruction = step.getString("html_instructions");
                        String stepDistance = step.getJSONObject("distance").getString("text");
                        String stepDuration = step.getJSONObject("duration").getString("text");

                        System.out.println("导航步骤: " + instruction);
                        System.out.println("距离: " + stepDistance);
                        System.out.println("时长: " + stepDuration);
                        // 获取 polyline 信息
                        String encodedPolyline = step.getJSONObject("polyline").getString("points");

                        // 解码 polyline
                        List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(encodedPolyline);
                        // 将 com.google.maps.model.LatLng 转换为 com.google.android.gms.maps.model.LatLng
                        List<LatLng> googleMapLatLngList = new ArrayList<>();
                        for (com.google.maps.model.LatLng point : decodedPath) {
                            googleMapLatLngList.add(new LatLng(point.lat, point.lng));  // 转换为 Google Maps API 的 LatLng
                        }
                        result.addAll(googleMapLatLngList);
                        // 输出每个解码后的坐标点
                        System.out.println("Decoded path for step " + (j + 1) + ":");
                        System.out.println("---------------------------");
                    }
                }
                return result;
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public interface DirectionsCallback {
        void onSuccess(List<LatLng> routes);
        void onFailure(Exception e);
    }
}