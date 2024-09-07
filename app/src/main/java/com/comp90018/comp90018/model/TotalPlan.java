package com.comp90018.comp90018.model;

import java.util.Date;
import java.util.List;

public class TotalPlan {
    private List<DayPlan> dayPlans;
    private Date startDate;
    private Date endDate;

    public TotalPlan(List<DayPlan> dayPlans, Date startDate, Date endDate) {
        this.dayPlans = dayPlans;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public TotalPlan() {
    }

    public List<DayPlan> getDayPlans() {
        return dayPlans;
    }

    public void setDayPlans(List<DayPlan> dayPlans) {
        this.dayPlans = dayPlans;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
