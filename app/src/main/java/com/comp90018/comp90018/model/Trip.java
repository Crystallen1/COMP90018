// app/src/main/java/com/comp90018/comp90018/model/Trip.java
package com.comp90018.comp90018.model;

public class Trip {
    private String title;
    private String startDate;
    private String endDate;
    private String location;
    private String remarks;
    private int thumbnailResId; // 本地资源图片 ID

    public Trip(String title, String startDate, String endDate, String location, String remarks, int thumbnailResId) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.remarks = remarks;
        this.thumbnailResId = thumbnailResId;
    }

    // Getter 方法
    public String getTitle() {
        return title;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getLocation() {
        return location;
    }

    public String getRemarks() {
        return remarks;
    }

    public int getThumbnailResId() {
        return thumbnailResId;
    }
}
