package com.comp90018.comp90018.service;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.comp90018.comp90018.model.DayPlan;
import com.comp90018.comp90018.model.Journey;
import com.comp90018.comp90018.model.TotalPlan;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.checkerframework.checker.lock.qual.LockHeld;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
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

    private static GPTService instance;

    public static GPTService getInstance(){
        if (instance==null){
            synchronized (GPTService.class){
                if(instance==null){
                    instance=new GPTService();
                }
            }
        }
        return instance;
    }


    private GPTService() {
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
                messages.put(new JSONObject().put("role", "user").put("content", "Now I want to visit " + name + ". Could you give me some introductions and travel recommendations? I mainly want to know some information about its history and ticket." + notes));

                jsonRequest.put("messages", messages);
//                jsonRequest.put("max_tokens", 100);

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
            contentArray.put(new JSONObject().put("type", "text").put("text", "This is a photo I took right now, and my position is  "
                    + latitude + " and longitude: " + longitude + "."+" Could you give me an introduction to the attraction in my photo and some travel tips for it?"));
            contentArray.put(new JSONObject().put("type", "image_url").put("image_url", new JSONObject().put("url", imageUrl)));

// 添加到 user 的 message
            messages.put(new JSONObject().put("role", "user").put("content", contentArray));
            jsonRequest.put("messages", messages);
//            jsonRequest.put("max_tokens", 100);

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

    public void getDayJourneyPlan(TotalPlan totalPlan,GPTCallback callback){
        apiKeyFuture.thenAccept(apiKey->{
            try {
                JSONObject jsonRequest = new JSONObject();
                jsonRequest.put("model", "gpt-3.5-turbo-0125");

                Date startDate = totalPlan.getStartDate();
                Map<String,Journey> targetPlaceMap = totalPlan.getTargetViewPoint();
                String city = totalPlan.getCity();
                String mode =totalPlan.getMode();
                int duration = totalPlan.getDuration();

                StringBuilder promptBuilder = new StringBuilder();
                // 构建 prompt 字符串
                promptBuilder.append("I am planning a trip to ")
                        .append(city)
                        .append(", starting on ")
                        .append(startDate)
                        .append(". The trip will last for ")
                        .append(duration)
                        .append(" days, and ");
                if(mode.equals("Leisure")){
                    promptBuilder.append("I want to travel with a relaxing schedule so I can focus on rest and in-depth experiences.");
                } else if (mode.equals("Medium")) {
                    promptBuilder.append("I want to travel with a reasonable amount of planning and rest time so I can find a balance between the number of attractions and the depth of their experiences.");
                }else if (mode.equals("Blitz")) {
                    promptBuilder.append("I want to travel with a tight, fast-paced schedule where I can visit multiple attractions in a short amount of time");
                }

                promptBuilder
                        .append(". I have a list of potential places to visit, each with a unique identifier. ")
                        .append("Please recommend which places I should visit on the first day of my trip.\n\n");
                promptBuilder.append("Here is the list of places, along with their unique identifiers:\n");
                for (Map.Entry<String, Journey> entry : targetPlaceMap.entrySet()) {
                    promptBuilder.append(entry.getKey()).append(": ").append(entry.getValue().getName()).append("\n");
                }
                promptBuilder.append("\nPlease return only the place identifiers for the places I should visit on the first day. Your response should only include the list of IDs, without additional explanations.");

                // 构建消息数组
                JSONArray messages = new JSONArray();
                messages.put(new JSONObject().put("role", "system").put("content", "You are a helpful tourist guide."));
                messages.put(new JSONObject().put("role", "user").put("content", promptBuilder.toString()));

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
                                    Log.d("GPTService",result);
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
                });}catch (Exception e){
                Log.e(TAG, "JSON parsing/creation error: " + e.getMessage(), e);
                callback.onFailure("Error generating journey introduction due to JSON error.");
            }
        }).exceptionally(throwable -> {
            Log.e("GPTService",throwable.getMessage());
            return null;
        });
    }

    public void getDayJourneyPlanByFeedback(TotalPlan totalPlan,int satisfaction,int tiredLevel, String feedback, int stepCount ,GPTCallback callback){
        apiKeyFuture.thenAccept(apiKey->{
            try {
                JSONObject jsonRequest = new JSONObject();
                jsonRequest.put("model", "gpt-3.5-turbo-0125");

                Date startDate = totalPlan.getStartDate();
                Map<String,Journey> targetPlaceMap = totalPlan.getTargetViewPoint();
                List<DayPlan> dayPlan = totalPlan.getDayPlans();
                List<Journey> journeysViewed = new ArrayList<>();
                for (DayPlan dayPlan1 : dayPlan) {
                    journeysViewed.addAll(dayPlan1.getJourneys());
                }
                String city = totalPlan.getCity();
                String mode =totalPlan.getMode();
                int duration = totalPlan.getDuration();

                Map<String, String> ModeDescription = new HashMap<>();
                ModeDescription.put("Blitz", "I want to travel with a tight, fast-paced schedule where I can visit multiple attractions in a short amount of time.");
                ModeDescription.put("Medium", "I want to travel with a reasonable amount of planning and rest time so I can find a balance between the number of attractions and the depth of their experiences.");
                ModeDescription.put("Leisure", "I want to travel with a relaxing schedule so I can focus on rest and in-depth experiences.");


                StringBuilder promptBuilder = new StringBuilder();
                promptBuilder.append("This is the travel city, travel pace and travel duration I selected.\n");
                promptBuilder.append("City: ").append(city).append("\n");
                promptBuilder.append("Travel Method: ").append(ModeDescription.get(mode)).append("\n");
                promptBuilder.append("Travel Duration: ").append(duration).append("\n");
                promptBuilder.append("This is my visited attraction\n");
                promptBuilder.append("Visited Attractions:\n");
                for (Journey attraction : journeysViewed) {
                    promptBuilder.append("  - ")
                            .append(": ").append(attraction).append("\n");
                }
                promptBuilder.append("This is my feedback on yesterday's trip:\n");
                promptBuilder.append("Yesterday's Step Count: ").append(stepCount).append("\n");
                promptBuilder.append("Satisfaction Level: ").append(satisfaction).append("\n");
                promptBuilder.append("Tiredness Level: ").append(tiredLevel).append("\n");
                promptBuilder.append("Additional Feedback: ").append(feedback).append("\n");
                promptBuilder.append("These are attractions I am interested in, including both visited and not visited.\n");
                promptBuilder.append("Desired Attractions (including those visited yesterday):\n");

                for (Map.Entry<String, Journey> attraction : targetPlaceMap.entrySet()) {
                    promptBuilder.append("  - ").append("Attraction ID:").append(attraction.getKey())
                            .append(", Details: ").append(attraction.getValue()).append("\n");

                }

                promptBuilder.append("Please recommend the attraction numbers to visit tomorrow from the desired attractions which " +
                        "not in the visited attractions list. The recommendation should be based on the additional feedback, " +
                        "step counts, satisfaction level and desired Attractions. \n");
                promptBuilder.append("\nPlease return only the Attraction ID of desired attractions I need to visit on the next day. " +
                        "Your response should only include the list of IDs, without additional explanations. " +
                        "The format of response should belike 1,2,3");
                // 构建消息数组
                JSONArray messages = new JSONArray();
                messages.put(new JSONObject().put("role", "system").put("content", "You are a helpful tourist guide."));
                messages.put(new JSONObject().put("role", "user").put("content", promptBuilder.toString()));

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
                                    Log.d("GPTService",result);
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
                });}catch (Exception e){
                Log.e(TAG, "JSON parsing/creation error: " + e.getMessage(), e);
                callback.onFailure("Error generating journey introduction due to JSON error.");
            }
        }).exceptionally(throwable -> {
            Log.e("GPTService",throwable.getMessage());
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
