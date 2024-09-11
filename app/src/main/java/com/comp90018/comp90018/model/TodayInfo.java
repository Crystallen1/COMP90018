package com.comp90018.comp90018.model;

public class TodayInfo {
    private int stepCount;
    private double humidity;
    private double temperature;
    private String weatherDescription;

    public TodayInfo(int stepCount, double humidity, double temperature, String weatherDescription) {
        this.stepCount = stepCount;
        this.humidity = humidity;
        this.temperature = temperature;
        this.weatherDescription = weatherDescription;
    }

    public TodayInfo() {
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }
}
