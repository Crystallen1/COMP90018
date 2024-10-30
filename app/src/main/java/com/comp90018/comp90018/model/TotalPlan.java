package com.comp90018.comp90018.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TotalPlan implements Parcelable {
    private List<DayPlan> dayPlans = new ArrayList<>();
    private Map<String,Journey> targetViewPoint = new HashMap<>();
    private Date startDate;
    private Date endDate;
    private int duration;
    private String city;
    private String name;
    private String mode;

    public TotalPlan() {
    }

    public Map<String, Journey> getTargetViewPoint() {
        return targetViewPoint;
    }

    public void setTargetViewPoint(Map<String, Journey> targetViewPoint) {
        this.targetViewPoint = targetViewPoint;
    }

    public String toString() {
        return "TotalPlan{" +
                "dayPlans=" + dayPlansToString() +
                ", targetViewPoint=" + targetViewPointToString() +
                ", startDate=" + (startDate != null ? startDate.toString() : "N/A") +
                ", endDate=" + (endDate != null ? endDate.toString() : "N/A") +
                ", duration=" + duration +
                ", city='" + (city != null ? city : "N/A") + '\'' +
                ", name='" + (name != null ? name : "N/A") + '\'' +
                ", mode='" + (mode != null ? mode : "N/A") + '\'' +
                '}';
    }

    // 辅助方法：将 dayPlans 列表转换为字符串
    private String dayPlansToString() {
        StringBuilder sb = new StringBuilder("[");
        for (DayPlan dayPlan : dayPlans) {
            sb.append(dayPlan.toString()).append(", ");
        }
        return sb.length() > 1 ? sb.substring(0, sb.length() - 2) + "]" : "[]";
    }

    // 辅助方法：将 targetViewPoint Map 转换为字符串
    private String targetViewPointToString() {
        StringBuilder sb = new StringBuilder("{");
        for (Map.Entry<String, Journey> entry : targetViewPoint.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue().toString()).append(", ");
        }
        return sb.length() > 1 ? sb.substring(0, sb.length() - 2) + "}" : "{}";
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<DayPlan> getDayPlans() {
        return dayPlans;
    }

    public void setDayPlans(List<DayPlan> dayPlans) {
        this.dayPlans = dayPlans;
    }
    public void addDayPlans(DayPlan dayPlans) {
        this.dayPlans.add(dayPlans);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(dayPlans);
        dest.writeInt(targetViewPoint.size());
        for (Map.Entry<String, Journey> entry : targetViewPoint.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeParcelable(entry.getValue(), flags);
        }
        dest.writeLong(startDate != null ? startDate.getTime() : -1L);
        dest.writeLong(endDate != null ? endDate.getTime() : -1L);
        dest.writeInt(duration);
        dest.writeString(city);
        dest.writeString(name);
        dest.writeString(mode);
    }

    // Constructor for creating from Parcel
    protected TotalPlan(Parcel in) {
        dayPlans = in.createTypedArrayList(DayPlan.CREATOR);
        int targetViewPointSize = in.readInt();
        targetViewPoint = new HashMap<>();
        for (int i = 0; i < targetViewPointSize; i++) {
            String key = in.readString();
            Journey journey = in.readParcelable(Journey.class.getClassLoader());
            targetViewPoint.put(key, journey);
        }
        long tmpStartDate = in.readLong();
        startDate = tmpStartDate == -1 ? null : new Date(tmpStartDate);
        long tmpEndDate = in.readLong();
        endDate = tmpEndDate == -1 ? null : new Date(tmpEndDate);
        duration = in.readInt();
        city = in.readString();
        name = in.readString();
        mode = in.readString();
    }

    public static final Creator<TotalPlan> CREATOR = new Creator<TotalPlan>() {
        @Override
        public TotalPlan createFromParcel(Parcel in) {
            return new TotalPlan(in);
        }

        @Override
        public TotalPlan[] newArray(int size) {
            return new TotalPlan[size];
        }
    };

}
