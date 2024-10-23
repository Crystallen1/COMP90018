package com.comp90018.comp90018.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Journey implements Parcelable {
    private String id;  // 文档 ID
    private String imageUrl;  // 图片的 URL
    private String name;      // 地点名称
    private String notes;     // 备注信息
    private double Latitude;  // 纬度
    private double Longitude; // 经度

    @Override
    public String toString() {
        return "Journey{" +
                "id='" + id + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", name='" + name + '\'' +
                ", notes='" + notes + '\'' +
                ", latitude=" + Latitude +
                ", longitude=" + Longitude +
                '}';
    }

    // 构造函数
    public Journey(String name, String notes, double latitude, double longitude) {
        this.name = name;
        this.notes = notes;
        this.Latitude = latitude;
        this.Longitude = longitude;
    }

    // 无参构造函数
    public Journey() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Journey journey = (Journey) o;

        return id != null ? id.equals(journey.id) : journey.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }


    // Getter 和 Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        this.Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        this.Longitude = longitude;
    }

    // Parcelable 部分

    // 从 Parcel 读取数据的构造函数
    protected Journey(Parcel in) {
        id = in.readString();
        imageUrl = in.readString();
        name = in.readString();
        notes = in.readString();
        Latitude = in.readDouble();
        Longitude = in.readDouble();
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(imageUrl);
        parcel.writeString(name);
        parcel.writeString(notes);
        parcel.writeDouble(Latitude);
        parcel.writeDouble(Longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // CREATOR 字段，用于从 Parcel 中创建 Journey 对象
    public static final Creator<Journey> CREATOR = new Creator<Journey>() {
        @Override
        public Journey createFromParcel(Parcel in) {
            return new Journey(in);
        }

        @Override
        public Journey[] newArray(int size) {
            return new Journey[size];
        }
    };
}