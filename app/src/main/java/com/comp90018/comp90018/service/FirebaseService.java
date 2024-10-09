package com.comp90018.comp90018.service;

import com.comp90018.comp90018.model.TotalPlan;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FirebaseService {
    private FirebaseFirestore db;
    private static FirebaseService instance;

    public static FirebaseService getInstance(){
        if(instance==null){
            synchronized (FirebaseService.class){
                if (instance==null){
                    instance=new FirebaseService();
                }
            }
        }
        return instance;
    }

    private FirebaseService() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * 上传 TotalPlan 到 Firebase Firestore
     *
     * @param totalPlan 要上传的 TotalPlan 对象
     * @param onSuccess 成功时的回调
     * @param onFailure 失败时的回调
     */
    public void uploadTotalPlan(TotalPlan totalPlan, String userId, OnSuccessListener<DocumentReference> onSuccess, OnFailureListener onFailure) {
//        String userId = auth.getCurrentUser().getUid(); // 获取当前用户ID

        db.collection("users").document(userId).collection("userPlans")
                .add(totalPlan)  // 自动生成唯一的文档 ID
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getAllTotalPlans(String userId, OnSuccessListener<List<TotalPlan>> onSuccess, OnFailureListener onFailure) {
        db.collection("users").document(userId).collection("userPlans")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<TotalPlan> totalPlans = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        TotalPlan totalPlan = document.toObject(TotalPlan.class); // 将文档转换为 TotalPlan 对象
                        if (totalPlan != null) {
                            totalPlans.add(totalPlan); // 添加到列表中
                        }
                    }
                    onSuccess.onSuccess(totalPlans); // 调用回调，返回 TotalPlan 列表
                })
                .addOnFailureListener(onFailure); // 如果出错，调用失败回调
    }

}
