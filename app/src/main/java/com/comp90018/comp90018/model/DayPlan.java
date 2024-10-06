package com.comp90018.comp90018.model;

import java.util.Date;
import java.util.List;

public class DayPlan {
    private Date date;
    private List<Journey> journeys;


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
}
