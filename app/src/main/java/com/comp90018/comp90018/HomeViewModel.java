package com.comp90018.comp90018;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.comp90018.comp90018.model.TotalPlan;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<TotalPlan> totalPlanMutableLiveData = new MutableLiveData<>(new TotalPlan());

    public LiveData<TotalPlan> getTotalPlan(){
        return totalPlanMutableLiveData;
    }
    public void updateLiveData(TotalPlan totalPlan){
        totalPlanMutableLiveData.setValue(totalPlan);
    }
}
