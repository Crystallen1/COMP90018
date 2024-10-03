package com.comp90018.comp90018.service;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.concurrent.CountDownLatch;

public class ApiKeyService {

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    private boolean activate;

    public ApiKeyService() {
        // Initialize Firebase Remote Config
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
    }

    // Interface for the callback to handle the async result
    public interface ApiKeyCallback {
        void onApiKeyRetrieved(String apiKey);
        void onFailure(Exception e);
    }

    // Method to fetch the API key asynchronously
    public void getApiKey(String apiName , ApiKeyCallback callback) {
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()&&activate) {
                        // Retrieve the API key from the Remote Config
                        String apiKey = mFirebaseRemoteConfig.getString(apiName);
                        // Pass the result to the callback
                        callback.onApiKeyRetrieved(apiKey);
                    } else {
                        // Handle the failure and pass it to the callback
                        callback.onFailure(new Exception("Failed to fetch API key"));
                    }
                });
    }

    public void enable(){
        this.activate=true;
    }

    public void disable(){
        this.activate=false;
    }
}
