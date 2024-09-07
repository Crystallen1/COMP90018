package com.comp90018.comp90018.model;

import java.util.Date;
import java.util.List;

public class User {
    private String username;
    private Integer Id;
    private Date birthDay;
    private String gender;
    private List<TotalPlan> totalPlans;

    public User(String username, Integer id, String gender, Date birthDay, List<TotalPlan> totalPlans) {
        this.username = username;
        Id = id;
        this.gender = gender;
        this.birthDay = birthDay;
        this.totalPlans = totalPlans;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<TotalPlan> getTotalPlans() {
        return totalPlans;
    }

    public void setTotalPlans(List<TotalPlan> totalPlans) {
        this.totalPlans = totalPlans;
    }
}
