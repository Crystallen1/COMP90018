package com.comp90018.comp90018.model;

import java.util.List;

public class DetailedList {
    private String name;
    private List<User> targetPeople;

    public DetailedList(List<User> targetPeople, String name) {
        this.targetPeople = targetPeople;
        this.name = name;
    }

    public DetailedList() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getTargetPeople() {
        return targetPeople;
    }

    public void setTargetPeople(List<User> targetPeople) {
        this.targetPeople = targetPeople;
    }
}
