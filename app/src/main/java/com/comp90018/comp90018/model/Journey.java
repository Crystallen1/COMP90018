package com.comp90018.comp90018.model;

import java.util.Date;

public class Journey {
    private String id;  // 文档 ID
    private String imageUrl;  // 图片的 URL

    private String name;         // 地点名称
    private String notes;        // 备注信息
    private double latitude;     // 纬度
    private double longitude;    // 经度

    public Journey(String name, String notes, double latitude, double longitude) {
        this.name = name;
        this.notes = notes;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Journey() {
    }

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
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}
