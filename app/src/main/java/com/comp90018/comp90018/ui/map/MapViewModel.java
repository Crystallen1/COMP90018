package com.comp90018.comp90018.ui.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;

public class MapViewModel extends ViewModel {

    private final MutableLiveData<HashMap<String, String>> journeyIntroductions = new MutableLiveData<>(new HashMap<>());

    // 获取介绍信息
    public LiveData<HashMap<String, String>> getJourneyIntroductions() {
        return journeyIntroductions;
    }
    // 添加或更新介绍信息
    public void addIntroduction(String journeyName, String introduction) {
        HashMap<String, String> currentIntroductions = journeyIntroductions.getValue();
        if (currentIntroductions != null) {
            currentIntroductions.put(journeyName, introduction);
            journeyIntroductions.setValue(currentIntroductions);  // 通知观察者数据已更新
        }
    }

    // 检查是否已经存在缓存的介绍
    public boolean hasIntroduction(String journeyName) {
        HashMap<String, String> currentIntroductions = journeyIntroductions.getValue();
        return currentIntroductions != null && currentIntroductions.containsKey(journeyName);
    }

    // 获取缓存的介绍
    public String getIntroduction(String journeyName) {
        HashMap<String, String> currentIntroductions = journeyIntroductions.getValue();
        return currentIntroductions != null ? currentIntroductions.get(journeyName) : null;
    }

}
