package com.stark.jsonproject;

/**
 * Created by DrMoo on 3/20/2018.
 */

public class Weather {

    public String city;
    public String country;
    public String state;
    public String fullName;
    public String observation_time;
    public String temperature;
    public String iconDescription;
    public String iconURL;
    public String[] alerts;
    public String[] hourlyHour;
    public String[] hourlyTemp;
    public String[] hourlyCondition;
    public String[] weeklyDay;
    public String[] weeklyHigh;
    public String[] weeklyLow;
    public String[] weeklyCondition;

    public Weather() {
    }

    public Weather(String city, String state,String country, String fullName, String observation_time, String temperature, String iconDescription, String iconURL, String[] alerts, String[] hourlyHour, String[] hourlyTemp, String[] hourlyCondition, String[] weeklyDay, String[] weeklyHigh, String[] weeklyLow, String[] weeklyCondition) {
        this.city = city;
        this.country = country;
        this.state = state;
        this.fullName = fullName;
        this.observation_time = observation_time;
        this.temperature = temperature;
        this.iconDescription = iconDescription;
        this.iconURL = iconURL;
        this.alerts = alerts;
        this.hourlyHour = hourlyHour;
        this.hourlyTemp = hourlyTemp;
        this.hourlyCondition = hourlyCondition;
        this.weeklyDay = weeklyDay;
        this.weeklyHigh = weeklyHigh;
        this.weeklyLow = weeklyLow;
        this.weeklyCondition = weeklyCondition;
    }
}
