package com.comp90018.comp90018.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class NavigationStep {
    private int sequenceId;
    private String instruction;
    private String stepDistance;
    private String stepDuration;
    private List<LatLng> routes = new ArrayList<>();

    public int getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(int sequenceId) {
        this.sequenceId = sequenceId;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getStepDistance() {
        return stepDistance;
    }

    public void setStepDistance(String stepDistance) {
        this.stepDistance = stepDistance;
    }

    public String getStepDuration() {
        return stepDuration;
    }

    public void setStepDuration(String stepDuration) {
        this.stepDuration = stepDuration;
    }

    public List<LatLng> getRoutes() {
        return routes;
    }

    public void setRoutes(List<LatLng> routes) {
        this.routes = routes;
    }

    public void addRoutes(LatLng latLng){
        this.routes.add(latLng);
    }
}
