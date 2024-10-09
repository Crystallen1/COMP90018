package com.comp90018.comp90018.model;

public class Plan {
    private String time;
    private String locationTitle;
    private String description;

    public Plan(String time, String locationTitle, String description) {
        this.time = time;
        this.locationTitle = locationTitle;
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public String getLocationTitle() {
        return locationTitle;
    }

    public String getDescription() {
        return description;
    }
}

