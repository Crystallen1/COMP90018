package com.comp90018.comp90018.service;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.comp90018.comp90018.model.Journey;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class ViewpointService {

    private static final String TAG = "ViewpointService";
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;

    public ViewpointService() {
        // 初始化 Firestore 和 Storage
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    // 定义回调接口
    public interface JourneyCallback {
        void onSuccess(List<Journey> journeys);
        void onFailure(String error);
    }

    // 获取指定城市的所有 Journey 数据和图片
    public void getJourneysByCity(String cityName, final JourneyCallback callback) {
        firestore.collection("journey")
                .whereEqualTo("city", cityName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Journey> journeyDataList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // 获取文档 ID
                                final String documentId = document.getId();
                                // 获取文档中的数据
                                Journey journeyData = document.toObject(Journey.class);
                                journeyData.setId(documentId);

                                // 从 Storage 获取图片
                                StorageReference imageRef = storage.getReference().child("journey_images/" + documentId + ".jpg");

                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // 获取图片的下载 URL 并设置到 journeyData
                                        journeyData.setImageUrl(uri.toString());
                                        journeyDataList.add(journeyData);

                                        // 如果所有文档的图片都获取成功，调用回调
                                        if (journeyDataList.size() == task.getResult().size()) {
                                            callback.onSuccess(journeyDataList);
                                        }
                                    }
                                }).addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to get image for document: " + documentId, e);
                                    callback.onFailure("Failed to get image for document: " + documentId);
                                });
                            }
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                            callback.onFailure("Error getting documents: " + task.getException());
                        }
                    }
                });
    }
}
