package com.comp90018.comp90018.model;

import java.util.List;

public class Trip {
    private TotalPlan totalPlan;
    private List<DetailedList> detailedLists;
    private List<Bill> bills;
    private TodayInfo todayInfo;

    public Trip(TotalPlan totalPlan, List<DetailedList> detailedLists, List<Bill> bills, TodayInfo todayInfo) {
        this.totalPlan = totalPlan;
        this.detailedLists = detailedLists;
        this.bills = bills;
        this.todayInfo = todayInfo;
    }

    public Trip() {
    }

    public List<DetailedList> getDetailedLists() {
        return detailedLists;
    }

    public void setDetailedLists(List<DetailedList> detailedLists) {
        this.detailedLists = detailedLists;
    }

    public TotalPlan getTotalPlan() {
        return totalPlan;
    }

    public void setTotalPlan(TotalPlan totalPlan) {
        this.totalPlan = totalPlan;
    }

    public List<Bill> getBills() {
        return bills;
    }

    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }

    public TodayInfo getTodayInfo() {
        return todayInfo;
    }

    public void setTodayInfo(TodayInfo todayInfo) {
        this.todayInfo = todayInfo;
    }
}
