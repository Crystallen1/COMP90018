package com.comp90018.comp90018.service;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
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

    private List<String> parseDirectionsResponse(String jsonResponse) {
        // 解析JSON并返回Route列表
        // 可使用Gson或其他JSON解析库进行解析
        // 返回Route对象或Polyline数据
        Log.d("NavigationService",jsonResponse);
        return null;
    }

    public interface DirectionsCallback {
        void onSuccess(List<String> routes);
        void onFailure(Exception e);
    }
}