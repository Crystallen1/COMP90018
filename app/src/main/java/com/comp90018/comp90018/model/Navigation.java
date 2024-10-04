package com.comp90018.comp90018.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Navigation {
    private String distance;
    private String duration;
    private String startAddress;
    private String endAddress;
    private List<NavigationStep> navigationSteps = new ArrayList<>();


    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public List<NavigationStep> getNavigationSteps() {
        return navigationSteps;
    }

    public void setNavigationSteps(List<NavigationStep> navigationSteps) {
        this.navigationSteps = navigationSteps;
    }

    public void addNavigationSteps(NavigationStep navigationStep){
        this.navigationSteps.add(navigationStep);
    }
}
