//package com.comp90018.comp90018.service;
//
//import com.comp90018.comp90018.model.TotalPlan;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class FirebaseService {
//    private FirebaseFirestore db;
//    private static FirebaseService instance;
//
//    public static FirebaseService getInstance(){
//        if(instance==null){
//            synchronized (FirebaseService.class){
//                if (instance==null){
//                    instance=new FirebaseService();
//                }
//            }
//        }
//        return instance;
//    }
//
//    private FirebaseService() {
//        db = FirebaseFirestore.getInstance();
//    }
//
//    /**
//     * 上传 TotalPlan 到 Firebase Firestore
//     *
//     * @param totalPlan 要上传的 TotalPlan 对象
//     * @param onSuccess 成功时的回调
//     * @param onFailure 失败时的回调
//     */
//    public void uploadTotalPlan(TotalPlan totalPlan, String userId, OnSuccessListener<DocumentReference> onSuccess, OnFailureListener onFailure) {
////        String userId = auth.getCurrentUser().getUid(); // 获取当前用户ID
//
//        db.collection("users").document(userId).collection("userPlans")
//                .add(totalPlan)  // 自动生成唯一的文档 ID
//                .addOnSuccessListener(onSuccess)
//                .addOnFailureListener(onFailure);
//    }
//
//    public void getAllTotalPlans(String userId, OnSuccessListener<List<TotalPlan>> onSuccess, OnFailureListener onFailure) {
//        db.collection("users").document(userId).collection("userPlans")
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    List<TotalPlan> totalPlans = new ArrayList<>();
//                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
//                        TotalPlan totalPlan = document.toObject(TotalPlan.class); // 将文档转换为 TotalPlan 对象
//                        if (totalPlan != null) {
//                            totalPlans.add(totalPlan); // 添加到列表中
//                        }
//                    }
//                    onSuccess.onSuccess(totalPlans); // 调用回调，返回 TotalPlan 列表
//                })
//                .addOnFailureListener(onFailure); // 如果出错，调用失败回调
//    }
//
//}
package com.comp90018.comp90018.service;

import com.comp90018.comp90018.model.DayPlan;
import com.comp90018.comp90018.model.TotalPlan;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FirebaseService {
    private FirebaseFirestore db;
    private static FirebaseService instance;

    // 单例模式：获取 FirebaseService 实例
    public static FirebaseService getInstance() {
        if (instance == null) {
            synchronized (FirebaseService.class) {
                if (instance == null) {
                    instance = new FirebaseService();
                }
            }
        }
        return instance;
    }

    // 私有构造函数：初始化 Firebase Firestore
    private FirebaseService() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * 上传 TotalPlan 到 Firebase Firestore
     *
     * @param totalPlan 要上传的 TotalPlan 对象
     * @param userId    用户 ID
     * @param onSuccess 成功时的回调
     * @param onFailure 失败时的回调
     */
    public void uploadTotalPlan(TotalPlan totalPlan, String userId,
                                OnSuccessListener<DocumentReference> onSuccess,
                                OnFailureListener onFailure) {

        db.collection("users").document(userId).collection("userPlans")
                .add(totalPlan)  // 自动生成唯一的文档 ID
                .addOnSuccessListener(documentReference -> {
                    // 打印上传的 TotalPlan 和文档 ID
                    System.out.println("Uploaded TotalPlan: " + totalPlan.toString());
                    System.out.println("Document ID: " + documentReference.getId());
                    // 调用成功回调
                    onSuccess.onSuccess(documentReference);
                })
                .addOnFailureListener(e -> {
                    // 打印错误信息
                    System.err.println("Error uploading TotalPlan: " + e.getMessage());
                    // 调用失败回调
                    onFailure.onFailure(e);
                });
    }

    /**
     * 获取所有 TotalPlan 对象
     *
     * @param userId    用户 ID
     * @param onSuccess 成功时的回调，返回 TotalPlan 列表
     * @param onFailure 失败时的回调
     */
//    public void getAllTotalPlans(String userId,
//                                 OnSuccessListener<List<TotalPlan>> onSuccess,
//                                 OnFailureListener onFailure) {
//
//        db.collection("users").document(userId).collection("userPlans")
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    List<TotalPlan> totalPlans = new ArrayList<>();
//                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
//                        TotalPlan totalPlan = document.toObject(TotalPlan.class);
//                        if (totalPlan != null) {
//                            totalPlans.add(totalPlan);
//                            // 打印每个 TotalPlan 的内容
//                            System.out.println("Fetched TotalPlan: " + totalPlan.toString());
//                        }
//                    }
//                    // 打印总的计划数
//                    System.out.println("Total plans fetched: " + totalPlans.size());
//                    // 调用成功回调
//                    onSuccess.onSuccess(totalPlans);
//                })
//                .addOnFailureListener(e -> {
//                    // 打印错误信息
//                    System.err.println("Error fetching TotalPlans: " + e.getMessage());
//                    // 调用失败回调
//                    onFailure.onFailure(e);
//                });
//    }
    public void getAllTotalPlans(String userId,
                                 OnSuccessListener<List<TotalPlan>> onSuccess,
                                 OnFailureListener onFailure) {

        db.collection("users").document(userId).collection("userPlans")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<TotalPlan> totalPlans = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        try {
                            // 确保 DayPlan 和 Journey 被正确解析
                            TotalPlan totalPlan = document.toObject(TotalPlan.class);

                            if (totalPlan != null) {
                                for (DayPlan dayPlan : totalPlan.getDayPlans()) {
                                    if (dayPlan.getJourneys() == null) {
                                        dayPlan.setJourneys(new ArrayList<>());
                                    }
                                }
                                totalPlans.add(totalPlan);
                                System.out.println("Fetched TotalPlan: " + totalPlan);
                            }
                        } catch (Exception e) {
                            System.err.println("Error parsing TotalPlan: " + e.getMessage());
                        }
                    }

                    System.out.println("Total plans fetched: " + totalPlans.size());
                    onSuccess.onSuccess(totalPlans);
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error fetching TotalPlans: " + e.getMessage());
                    onFailure.onFailure(e);
                });
    }

    public void getOngoingPlans(String userId,
                                 OnSuccessListener<List<TotalPlan>> onSuccess,
                                 OnFailureListener onFailure) {

        db.collection("users").document(userId).collection("userPlans")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<TotalPlan> totalPlans = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        try {
                            // 确保 DayPlan 和 Journey 被正确解析
                            TotalPlan totalPlan = document.toObject(TotalPlan.class);

                            if (totalPlan != null) {
                                for (DayPlan dayPlan : totalPlan.getDayPlans()) {
                                    if (dayPlan.getJourneys() == null) {
                                        dayPlan.setJourneys(new ArrayList<>());
                                    }
                                }
                                if (totalPlan.getEndDate().compareTo(new Date())>=0){
                                    totalPlans.add(totalPlan);
                                }
                                System.out.println("Fetched TotalPlan: " + totalPlan);
                            }
                        } catch (Exception e) {
                            System.err.println("Error parsing TotalPlan: " + e.getMessage());
                        }
                    }

                    System.out.println("Total plans fetched: " + totalPlans.size());
                    onSuccess.onSuccess(totalPlans);
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error fetching TotalPlans: " + e.getMessage());
                    onFailure.onFailure(e);
                });
    }

}
