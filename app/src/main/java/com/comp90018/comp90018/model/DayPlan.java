package com.comp90018.comp90018.model;

import java.util.Date;
import java.util.List;

public class DayPlan {
    private Date date;
    private List<Journey> journeys;
    private String weatherDescription;

    public DayPlan(Date date, List<Journey> journeys, String weatherDescription) {
        this.date = date;
        this.journeys = journeys;
        this.weatherDescription = weatherDescription;
    }

    public DayPlan() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    public List<Journey> getJourneys() {
        return journeys;
    }

    public void setJourneys(List<Journey> journeys) {
        this.journeys = journeys;
    }
}
