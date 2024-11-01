
//package com.comp90018.comp90018.service;
//
//import android.net.Uri;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//
//import com.comp90018.comp90018.model.Journey;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ViewpointService {
//
//    private static final String TAG = "ViewpointService";
//    private FirebaseFirestore firestore;
//    private FirebaseStorage storage;
//
//    public ViewpointService() {
//        // 初始化 Firestore 和 Storage
//        firestore = FirebaseFirestore.getInstance();
//        storage = FirebaseStorage.getInstance();
//        Log.d(TAG, "Firestore 和 Storage 已初始化。");
//    }
//
//    // 定义回调接口
//    public interface JourneyCallback {
//        void onSuccess(List<Journey> journeys);
//        void onFailure(String error);
//    }
//
//    // 获取指定城市的所有 Journey 数据和图片
//    public void getJourneysByCity(String cityName, final JourneyCallback callback) {
//        Log.d(TAG, "获取城市：" + cityName + " 的 Journeys 数据。");
//        firestore.collection("Journey")
//                .whereEqualTo("city", cityName)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            int totalDocuments = task.getResult().size();
//                            Log.d(TAG, "成功获取 Firestore 文档，数量：" + totalDocuments);
//                            if (totalDocuments == 0) {
//                                // 如果没有文档，直接回调空列表
//                                Log.d(TAG, "没有找到匹配的文档，调用回调传递空列表。");
//                                callback.onSuccess(new ArrayList<>());
//                                return;
//                            }
//
//                            List<Journey> journeyDataList = new ArrayList<>();
//                            final int[] processedCount = {0}; // 使用数组以便在内部类中修改
//
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                // 获取文档 ID
//                                final String documentId = document.getId();
//                                Log.d(TAG, "处理文档 ID：" + documentId);
//
//                                // 获取文档中的数据
//                                Journey journeyData = document.toObject(Journey.class);
//                                journeyData.setId(documentId);
//
//                                // 从 Storage 获取图片
//                                StorageReference imageRef = storage.getReference().child("images/" + cityName + "/" + documentId + ".jpeg");
//                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                    @Override
//                                    public void onSuccess(Uri uri) {
//                                        // 获取图片的下载 URL 并设置到 journeyData
//                                        journeyData.setImageUrl(uri.toString());
//                                        journeyDataList.add(journeyData);
//                                        Log.d(TAG, "成功获取图片 URL：" + uri.toString() + "，当前已获取图片数量：" + journeyDataList.size());
//
//                                        // 更新已处理计数
//                                        processedCount[0]++;
//                                        checkAndCallback();
//                                    }
//
//                                    private void checkAndCallback() {
//                                        if (processedCount[0] == totalDocuments) {
//                                            Log.d(TAG, "所有文档已处理，调用回调。成功获取 " + journeyDataList.size() + " 条 Journey 数据。");
//                                            callback.onSuccess(journeyDataList);
//                                        }
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Log.e(TAG, "获取图片失败，文档 ID：" + documentId, e);
//                                        // 即使失败，也要更新已处理计数
//                                        processedCount[0]++;
//                                        checkAndCallback();
//                                    }
//
//                                    private void checkAndCallback() {
//                                        if (processedCount[0] == totalDocuments) {
//                                            Log.d(TAG, "所有文档已处理，调用回调。成功获取 " + journeyDataList.size() + " 条 Journey 数据。");
//                                            callback.onSuccess(journeyDataList);
//                                        }
//                                    }
//                                });
//                            }
//                        } else {
//                            Log.e(TAG, "Error getting documents: ", task.getException());
//                            callback.onFailure("Error getting documents: " + task.getException());
//                        }
//                    }
//                });
//    }
//}
// File: com/comp90018/comp90018/service/ViewpointService.java

package com.comp90018.comp90018.service;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.comp90018.comp90018.model.Journey;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ViewpointService {

    private static final String TAG = "ViewpointService";
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;

    public ViewpointService() {
        // 初始化 Firestore 和 Storage
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        Log.d(TAG, "Firestore 和 Storage 已初始化。");
    }

    // 定义回调接口
    public interface JourneyCallback {
        void onSuccess(List<Journey> journeys);
        void onFailure(String error);
    }

    // 获取指定城市的所有 Journey 数据和图片
    public void getJourneysByCity(String cityName, final JourneyCallback callback) {
        Log.d(TAG, "获取城市：" + cityName + " 的 Journeys 数据。");
        firestore.collection("Journey") // 确保集合名与 Firestore 中一致
                .whereEqualTo("city", cityName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int totalDocuments = task.getResult().size();
                            Log.d(TAG, "成功获取 Firestore 文档，数量：" + totalDocuments);
                            if (totalDocuments == 0) {
                                // 如果没有文档，直接回调空列表
                                Log.d(TAG, "没有找到匹配的文档，调用回调传递空列表。");
                                callback.onSuccess(new ArrayList<>());
                                return;
                            }

                            List<Journey> journeyDataList = new ArrayList<>();
                            AtomicInteger processedCount = new AtomicInteger(0);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // 获取文档 ID
                                final String documentId = document.getId();
                                Log.d(TAG, "处理文档 ID：" + documentId);

                                // 获取文档中的数据
                                Journey journeyData = document.toObject(Journey.class);
                                Log.d(TAG,journeyData.toString());
                                journeyData.setId(documentId);

                                // 从 Storage 获取图片
                                StorageReference imageRef = storage.getReference().child("images/" + cityName + "/" + documentId + ".jpeg");
                                imageRef.getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                // 获取图片的下载 URL 并设置到 journeyData
                                                journeyData.setImageUrl(uri.toString());
                                                journeyDataList.add(journeyData);
                                                Log.d(TAG, "成功获取图片 URL：" + uri.toString() + "，当前已获取图片数量：" + journeyDataList.size());

                                                // 更新已处理的文档数
                                                int currentCount = processedCount.incrementAndGet();

                                                // 如果所有文档的图片都处理完成，调用回调
                                                if (currentCount == totalDocuments) {
                                                    Log.d(TAG, "所有文档已处理完成，成功获取 " + journeyDataList.size() + " 条 Journey 数据。");
                                                    callback.onSuccess(journeyDataList);
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // 获取图片失败，记录失败的 URL 并跳过该 Journey
                                                Log.e(TAG, "获取图片失败，文档 ID：" + documentId + "，URL：images/" + cityName + "/" + documentId + ".jpeg", e);

                                                // 更新已处理的文档数
                                                int currentCount = processedCount.incrementAndGet();

                                                // 如果所有文档的图片都处理完成，调用回调
                                                if (currentCount == totalDocuments) {
                                                    Log.d(TAG, "所有文档已处理完成，成功获取 " + journeyDataList.size() + " 条 Journey 数据。");
                                                    callback.onSuccess(journeyDataList);
                                                }
                                            }
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
