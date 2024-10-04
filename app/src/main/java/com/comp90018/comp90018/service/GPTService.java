package com.comp90018.comp90018.service;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GPTService {
    private static final String TAG = "GPTService";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    String API_KEY ;  // 从环境变量读取
    private final OkHttpClient client = new OkHttpClient();
    private ApiKeyService apiKeyService = new ApiKeyService();
    private CompletableFuture<String> apiKeyFuture;


    public GPTService() {
        apiKeyFuture = new CompletableFuture<>();
        apiKeyService.enable();
        apiKeyService.getApiKey("GPT_API_KEY", new ApiKeyService.ApiKeyCallback() {
            @Override
            public void onApiKeyRetrieved(String apiKey) {
                API_KEY = apiKey;
                Log.d(TAG,"Get the GPT api key:"+apiKey);
                apiKeyFuture.complete(apiKey);
            }
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG,"fail to get the gpt api key:"+e);
                apiKeyFuture.completeExceptionally(new RuntimeException("Failed to fetch API key"));
            }
        });
    }

    // 定义回调接口
    public interface GPTCallback {
        void onSuccess(String result);
        void onFailure(String error);
    }

    public void getJourneyIntroduction(String name, String notes, GPTCallback callback) {
        apiKeyFuture.thenAccept(apiKey -> {
            // 使用apiKey进行操作
            try {
                JSONObject jsonRequest = new JSONObject();
                jsonRequest.put("model", "gpt-3.5-turbo-0125");

                // 构建消息数组
                JSONArray messages = new JSONArray();
                messages.put(new JSONObject().put("role", "system").put("content", "You are a helpful tourist guide."));
                messages.put(new JSONObject().put("role", "user").put("content", "Generate an introduction and travel guide for the location: " + name + ". Here are some notes: " + notes));

                jsonRequest.put("messages", messages);
                jsonRequest.put("max_tokens", 100);

                RequestBody body = RequestBody.create(
                        jsonRequest.toString(), MediaType.get("application/json; charset=utf-8"));
                if (API_KEY==null){
                    Log.e(TAG,"APIKEY is null");
                }

                Request request = new Request.Builder()
                        .url(API_URL)
                        .post(body)
                        .addHeader("Authorization", "Bearer " + API_KEY)
                        .build();

                // 发起异步请求
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        String errorMessage = getErrorMessage(e);
                        // 在主线程中调用回调
                        runOnUiThread(() -> callback.onFailure(errorMessage));
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            runOnUiThread(() -> callback.onFailure("Unexpected code " + response));
                            return;
                        }

                        try {
                            // 处理 GPT 的返回结果
                            JSONObject jsonResponse = new JSONObject(response.body().string());
                            JSONArray choicesArray = jsonResponse.getJSONArray("choices");

                            if (choicesArray.length() > 0) {
                                JSONObject firstChoice = choicesArray.getJSONObject(0);
                                JSONObject messageObject = firstChoice.getJSONObject("message");
                                if (messageObject.has("content")) {
                                    String result = messageObject.getString("content").trim();
                                    // 在主线程中调用成功回调
                                    runOnUiThread(() -> callback.onSuccess(result));
                                } else {
                                    runOnUiThread(() -> callback.onFailure("No 'content' field in the message object"));
                                }
                            } else {
                                runOnUiThread(() -> callback.onFailure("Choices array is empty"));
                            }
                        } catch (JSONException e) {
                            runOnUiThread(() -> callback.onFailure("JSON parsing error: " + e.getMessage()));
                        }
                    }
                });} catch (JSONException e) {
                // 捕获 JSON 构造或解析的错误
                Log.e(TAG, "JSON parsing/creation error: " + e.getMessage(), e);
                callback.onFailure("Error generating journey introduction due to JSON error.");
            }
        }).exceptionally(throwable -> {
            Log.e("GPTService", throwable.getMessage());

            return null;
        });


    }

    public void getImageBasedJourneyIntroduction(String imageUrl, double latitude, double longitude,GPTCallback callback) {
        apiKeyFuture.thenAccept(apiKey -> {
                    try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("model", "gpt-4o");

            // Build the messages array
            JSONArray messages = new JSONArray();
            messages.put(new JSONObject().put("role", "system").put("content", "You are a knowledgeable travel guide specializing in scenic locations."));

// 构建 content 数组，包含文本和图片 URL
            JSONArray contentArray = new JSONArray();
            contentArray.put(new JSONObject().put("type", "text").put("text", "Provide an introduction and travel guide for the location based on this image and the latitude: "
                    + latitude + " and longitude: " + longitude + "."));
            contentArray.put(new JSONObject().put("type", "image_url").put("image_url", new JSONObject().put("url", imageUrl)));

// 添加到 user 的 message
            messages.put(new JSONObject().put("role", "user").put("content", contentArray));
            jsonRequest.put("messages", messages);
            jsonRequest.put("max_tokens", 100);

            RequestBody body = RequestBody.create(
                    jsonRequest.toString(), MediaType.get("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .build();
            // 发起异步请求
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    String errorMessage = getErrorMessage(e);
                    // 在主线程中调用回调
                    runOnUiThread(() -> callback.onFailure(errorMessage));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        runOnUiThread(() -> callback.onFailure("Unexpected code " + response));
                        return;
                    }

                    try {
                        // 处理 GPT 的返回结果
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        JSONArray choicesArray = jsonResponse.getJSONArray("choices");

                        if (choicesArray.length() > 0) {
                            JSONObject firstChoice = choicesArray.getJSONObject(0);
                            JSONObject messageObject = firstChoice.getJSONObject("message");
                            if (messageObject.has("content")) {
                                String result = messageObject.getString("content").trim();
                                // 在主线程中调用成功回调
                                runOnUiThread(() -> callback.onSuccess(result));
                            } else {
                                runOnUiThread(() -> callback.onFailure("No 'content' field in the message object"));
                            }
                        } else {
                            runOnUiThread(() -> callback.onFailure("Choices array is empty"));
                        }
                    } catch (JSONException e) {
                        runOnUiThread(() -> callback.onFailure("JSON parsing error: " + e.getMessage()));
                    }
                }
            });
        } catch (JSONException e) {
            // 捕获 JSON 构造或解析的错误
            Log.e(TAG, "JSON parsing/creation error: " + e.getMessage(), e);
            callback.onFailure("Error generating journey introduction due to JSON error.");
        }
    }).exceptionally(throwable -> {
            Log.e("GPTService", throwable.getMessage());
            return null;
        });
    }

    // 根据不同的异常类型返回合适的错误信息
    private String getErrorMessage(IOException e) {
        if (e instanceof SocketTimeoutException) {
            return "Network timeout. Please check your connection and try again.";
        } else if (e instanceof UnknownHostException) {
            return "Network error. Unable to reach the server. Please check your connection.";
        } else {
            return "Error generating journey introduction due to network issue: " + e.getMessage();
        }
    }

    // 切换到主线程运行代码
    private void runOnUiThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }
}
