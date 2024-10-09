package com.comp90018.comp90018;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.comp90018.comp90018.model.Journey;
import com.comp90018.comp90018.model.TotalPlan;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeViewModel extends ViewModel {
    private static final String TAG = "HomeViewModel";
    private final MutableLiveData<TotalPlan> totalPlanMutableLiveData = new MutableLiveData<>(new TotalPlan());
    private MutableLiveData<List<Journey>> journeysLiveData = new MutableLiveData<>();
    private MutableLiveData<Set<Journey>> wishListJourneys = new MutableLiveData<>(new HashSet<>());

    public LiveData<TotalPlan> getTotalPlan(){
        return totalPlanMutableLiveData;
    }
    public void updateLiveData(TotalPlan totalPlan){
        totalPlanMutableLiveData.setValue(totalPlan);
    }

    // 返回 LiveData 以便观察
    public LiveData<List<Journey>> getJourneys() {
        return journeysLiveData;
    }

    // 获取 Journey 列表
    public LiveData<Set<Journey>> getWishListJourneys() {
        return wishListJourneys;
    }

    // 设置 Journey 列表
    public void setWishListJourneys(Set<Journey> journeys) {
        this.wishListJourneys.setValue(journeys);
    }

    // 添加 Journey 到列表
    public void addWishListJourney(Journey journey) {
        Set<Journey> currentJourneys = wishListJourneys.getValue();
        if (currentJourneys != null) {
            currentJourneys.add(journey);
            wishListJourneys.setValue(currentJourneys);
        }
    }
    // 从 Wish List 中移除 Journey
    public void removeWishListJourney(Journey journey) {
        Set<Journey> currentJourneys = wishListJourneys.getValue();
        if (currentJourneys != null && currentJourneys.contains(journey)) {
            currentJourneys.remove(journey);
            wishListJourneys.setValue(currentJourneys);  // 通知观察者更新
        } else {
            Log.d(TAG, "Journey 不在 Wish List 中");
        }
    }

    // 清空 Journey 列表
    public void clearWishListJourneys() {
        wishListJourneys.setValue(null);
    }

    // 更新 Journey 数据
    public void setJourneys(List<Journey> journeys) {
        journeysLiveData.setValue(journeys);
    }

    // 清除 Journey 数据
    public void clearJourneys() {
        journeysLiveData.setValue(null);
    }
}
