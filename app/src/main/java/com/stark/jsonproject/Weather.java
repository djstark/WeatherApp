package com.stark.jsonproject;

/**
 * Created by DrMoo on 3/20/2018.
 */

public class Weather {

    public String fullName;
    public String observation_time;
    public String temperature;
    public String iconDescription;
    public String iconURL;
    public String[] alerts;

    public Weather() {
    }

    public Weather(String fullName, String observation_time, String temperature, String iconDescription, String iconURL, String[] alerts) {
        this.fullName = fullName;
        this.observation_time = observation_time;
        this.temperature = temperature;
        this.iconDescription = iconDescription;
        this.iconURL = iconURL;
        this.alerts = alerts;
    }
}
