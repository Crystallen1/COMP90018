package com.comp90018.comp90018.service;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GPTService {
    private static final String TAG = "GPTService";


    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    String API_KEY = System.getenv("OPENAI_API_KEY");  // 从环境变量读取

    private final OkHttpClient client = new OkHttpClient();

    public String getJourneyIntroduction(String name, String notes) {
        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("model", "gpt-3.5-turbo-0125");
            // Build the messages array
            JSONArray messages = new JSONArray();
            messages.put(new JSONObject().put("role", "system").put("content", "You are a helpful tourist guide."));
            messages.put(new JSONObject().put("role", "user").put("content", "Generate an introduction and travel guide for the location: " + name + ". Here are some notes: " + notes));

            jsonRequest.put("messages", messages);
            jsonRequest.put("max_tokens", 100);

            RequestBody body = RequestBody.create(
                    jsonRequest.toString(), MediaType.get("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                // 处理 GPT 的返回结果
                JSONObject jsonResponse = new JSONObject(response.body().string());
                JSONArray choicesArray = jsonResponse.getJSONArray("choices");

                if (choicesArray.length() > 0) {
                    JSONObject firstChoice = choicesArray.getJSONObject(0);
                    JSONObject messageObject = firstChoice.getJSONObject("message");
                    if (messageObject.has("content")) {
                        return messageObject.getString("content").trim();
                    } else {
                        throw new JSONException("No 'content' field in the message object");
                    }
                } else {
                    throw new JSONException("Choices array is empty");
                }
            }
        } catch (JSONException e) {
            // 捕获 JSON 构造或解析的错误
            Log.e(TAG, "JSON parsing/creation error: " + e.getMessage(), e);
            return "Error generating journey introduction due to JSON error.";

        } catch (SocketTimeoutException e) {
            // 捕获网络超时异常
            Log.e(TAG, "Network timeout: " + e.getMessage(), e);
            return "Network timeout. Please check your connection and try again.";

        } catch (UnknownHostException e) {
            // 捕获 DNS 解析错误或网络不可达问题
            Log.e(TAG, "Network connection error: " + e.getMessage(), e);
            return "Network error. Unable to reach the server. Please check your connection.";

        } catch (IOException e) {
            // 捕获网络IO错误
            Log.e(TAG, "IO error during API request: " + e.getMessage(), e);
            return "Error generating journey introduction due to network issue.";

        } catch (NullPointerException e) {
            // 捕获潜在的空指针错误
            Log.e(TAG, "NullPointerException: " + e.getMessage(), e);
            return "An unexpected error occurred. Please try again.";

        } catch (Exception e) {
            // 捕获其他未知的异常
            Log.e(TAG, "Unexpected error: " + e.getMessage(), e);
            return "Error generating journey introduction due to an unexpected issue.";
        }
    }
}
