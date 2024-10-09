package com.comp90018.comp90018.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class DayPlan implements Parcelable {
    private Date date;
    private List<Journey> journeys;

    public DayPlan() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Journey> getJourneys() {
        return journeys;
    }

    public void setJourneys(List<Journey> journeys) {
        this.journeys = journeys;
    }

    // Parcelable 构造函数
    protected DayPlan(Parcel in) {
        // 读取 long 值然后转换为 Date 对象
        long tmpDate = in.readLong();
        date = tmpDate == -1 ? null : new Date(tmpDate);

        // 读取 List<Journey>
        journeys = new ArrayList<>();
        in.readList(journeys, Journey.class.getClassLoader());
    }

    // 写入数据到 Parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // 写入 Date 对象为 long，如果为空，写入 -1
        dest.writeLong(date != null ? date.getTime() : -1);

        // 写入 List<Journey>
        dest.writeList(journeys);
    }

    // 返回内容描述符，默认返回 0
    @Override
    public int describeContents() {
        return 0;
    }

    // 用于创建 DayPlan 对象的 CREATOR
    public static final Parcelable.Creator<DayPlan> CREATOR = new Parcelable.Creator<DayPlan>() {
        @Override
        public DayPlan createFromParcel(Parcel in) {
            return new DayPlan(in);
        }

        @Override
        public DayPlan[] newArray(int size) {
            return new DayPlan[size];
        }
    };
}
