package com.comp90018.comp90018.model;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class DayPlan {
    private Date date;
    private Set<Journey> journeys;


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Set<Journey> getJourneys() {
        return journeys;
    }

    public void setJourneys(Set<Journey> journeys) {
        this.journeys = journeys;
    }
}
