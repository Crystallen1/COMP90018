package com.comp90018.comp90018.service;

import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;

public class ImageUploadService {

    private OkHttpClient client = new OkHttpClient();

    public interface UploadCallback {
        void onSuccess(String imageUrl);
        void onFailure(Exception e);
    }

    public void uploadImage(File photoFile, UploadCallback callback) {
        RequestBody fileBody = RequestBody.create(photoFile, MediaType.parse("image/jpeg"));
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", photoFile.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url("https://your-server.com/upload")  // 替换为你实际的服务器地址
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure(new IOException("Unexpected code " + response));
                    return;
                }

                String jsonResponse = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    String imageUrl = jsonObject.getString("url");
                    callback.onSuccess(imageUrl);
                } catch (JSONException e) {
                    callback.onFailure(e);
                }
            }
        });
    }
}