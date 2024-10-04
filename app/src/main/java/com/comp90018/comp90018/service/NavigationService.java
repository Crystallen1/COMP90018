package com.comp90018.comp90018.service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.comp90018.comp90018.model.Navigation;
import com.comp90018.comp90018.model.NavigationStep;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.maps.internal.PolylineEncoding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NavigationService {
    private static final String TAG = "NavigationService";
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/directions/json";
    String API_KEY;
    private ApiKeyService apiKeyService = new ApiKeyService();
    private CompletableFuture<String> apiKeyFuture;

    public NavigationService() {
        apiKeyFuture = new CompletableFuture<>();

        apiKeyService.enable();
        apiKeyService.getApiKey("MAP_API_KEY", new ApiKeyService.ApiKeyCallback() {
           @Override
           public void onApiKeyRetrieved(String apiKey) {
               API_KEY = apiKey;
               Log.d(TAG,"Get the map api key:"+apiKey);
               apiKeyFuture.complete(apiKey);
           }
           @Override
           public void onFailure(Exception e) {
                Log.e(TAG, "fail to get the map api key:"+e);
               apiKeyFuture.completeExceptionally(new RuntimeException("Failed to fetch API key"));

           }
       });
    }

    public void getDirections(String origin, String destination, DirectionsCallback callback) {
        apiKeyFuture.thenAccept(apiKey -> {
            // 使用apiKey进行操作
            OkHttpClient client = new OkHttpClient();
            if (API_KEY==null){
                Log.e(TAG,"APIKEY is null");
            }
            HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL).newBuilder();
            urlBuilder.addQueryParameter("origin", origin);
            urlBuilder.addQueryParameter("destination", destination);
            urlBuilder.addQueryParameter("key", API_KEY);

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
        }).exceptionally(throwable -> {
            Log.e("GPTService", throwable.getMessage());
            return null;
        });
    }

    public void getDirections(double originLat, double originLng, double destLat, double destLng, DirectionsCallback callback) {
        apiKeyFuture.thenAccept(apiKey -> {
            // 使用apiKey进行操作
            OkHttpClient client = new OkHttpClient();
            if (apiKey == null) {
                Log.e(TAG, "API key is null");
            }

            // 构建 URL，将经纬度拼接为 "latitude,longitude" 的形式
            HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL).newBuilder();
            urlBuilder.addQueryParameter("origin", originLat + "," + originLng);  // 起点经纬度
            urlBuilder.addQueryParameter("destination", destLat + "," + destLng); // 终点经纬度
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
        }).exceptionally(throwable -> {
            Log.e("GPTService", throwable.getMessage());
            return null;
        });
    }

    private Navigation parseDirectionsResponse(String jsonResponse) {
        // 解析JSON并返回Route列表
        // 可使用Gson或其他JSON解析库进行解析
        // 返回Route对象或Polyline数据
//        Log.d("NavigationService",jsonResponse);
        // 解析JSON
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);

            // 获取routes
            JSONArray routes = jsonObject.getJSONArray("routes");
            Navigation navigation = new Navigation();
            for(int i=0;i<routes.length();++i){
                JSONObject route = routes.getJSONObject(i);  // 只取第一条路线
                JSONArray legs = route.getJSONArray("legs");
                for (int j=0;j<legs.length();++j){
                    JSONObject leg = legs.getJSONObject(j);
                    navigation.setDistance(leg.getJSONObject("distance").getString("text"));
                    navigation.setDuration(leg.getJSONObject("duration").getString("text"));
                    navigation.setStartAddress(leg.getString("start_address"));
                    navigation.setEndAddress(leg.getString("end_address"));
                    System.out.println("起点: " + navigation.getStartAddress());
                    System.out.println("终点: " + navigation.getEndAddress());
                    System.out.println("距离: " + navigation.getDistance());
                    System.out.println("时长: " + navigation.getDuration());

                    JSONArray steps = leg.getJSONArray("steps");
                    for (int k = 0; k < steps.length(); k++) {
                        JSONObject step = steps.getJSONObject(k);

                        NavigationStep navigationStep = new NavigationStep();
                        navigationStep.setInstruction(step.getString("html_instructions"));
                        navigationStep.setStepDistance(step.getJSONObject("distance").getString("text"));
                        navigationStep.setStepDuration(step.getJSONObject("duration").getString("text"));

                        String encodedPolyline = step.getJSONObject("polyline").getString("points");
                        List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(encodedPolyline);

                        // 将 com.google.maps.model.LatLng 转换为 com.google.android.gms.maps.model.LatLng
                        List<LatLng> googleMapLatLngList = new ArrayList<>();
                        for (com.google.maps.model.LatLng point : decodedPath) {
                            googleMapLatLngList.add(new LatLng(point.lat, point.lng));  // 转换为 Google Maps API 的 LatLng
                        }
                        navigationStep.setRoutes(googleMapLatLngList);
                        // 输出每个解码后的坐标点
                        System.out.println("Decoded path for step " + (j + 1) + ":");
                        System.out.println("---------------------------");
                        navigation.addNavigationSteps(navigationStep);
                    }
                }
                return navigation;
            }
//            if (routes.length() > 0) {
//                JSONObject route = routes.getJSONObject(0);  // 只取第一条路线
//                JSONArray legs = route.getJSONArray("legs");
//                List<LatLng> result = new ArrayList<>();
//                for (int i = 0; i < legs.length(); i++) {
//                    JSONObject leg = legs.getJSONObject(i);
//
//                    // 获取距离和持续时间
//                    String distance = leg.getJSONObject("distance").getString("text");
//                    String duration = leg.getJSONObject("duration").getString("text");
//                    String startAddress = leg.getString("start_address");
//                    String endAddress = leg.getString("end_address");
//
//                    System.out.println("起点: " + startAddress);
//                    System.out.println("终点: " + endAddress);
//                    System.out.println("距离: " + distance);
//                    System.out.println("时长: " + duration);
//
//                    // 获取每一步的导航详情
//                    JSONArray steps = leg.getJSONArray("steps");
//                    for (int j = 0; j < steps.length(); j++) {
//                        JSONObject step = steps.getJSONObject(j);
//
//                        String instruction = step.getString("html_instructions");
//                        String stepDistance = step.getJSONObject("distance").getString("text");
//                        String stepDuration = step.getJSONObject("duration").getString("text");
//
//                        System.out.println("导航步骤: " + instruction);
//                        System.out.println("距离: " + stepDistance);
//                        System.out.println("时长: " + stepDuration);
//                        // 获取 polyline 信息
//                        String encodedPolyline = step.getJSONObject("polyline").getString("points");
//
//                        // 解码 polyline
//                        List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(encodedPolyline);
//                        // 将 com.google.maps.model.LatLng 转换为 com.google.android.gms.maps.model.LatLng
//                        List<LatLng> googleMapLatLngList = new ArrayList<>();
//                        for (com.google.maps.model.LatLng point : decodedPath) {
//                            googleMapLatLngList.add(new LatLng(point.lat, point.lng));  // 转换为 Google Maps API 的 LatLng
//                        }
//                        result.addAll(googleMapLatLngList);
//                        // 输出每个解码后的坐标点
//                        System.out.println("Decoded path for step " + (j + 1) + ":");
//                        System.out.println("---------------------------");
//                    }
//                }
//                return result;
//            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public interface DirectionsCallback {
        void onSuccess(Navigation routes);
        void onFailure(Exception e);
    }
}