package com.example.grades.Models;

import java.util.Date;

public class Forecast {
    private Date date;
    private long lowTemp, highTemp;
    private String weather;

    public Forecast(Date date, long lowTemp, long highTemp, String weather) {
        this.date = date;
        this.lowTemp = lowTemp;
        this.highTemp = highTemp;
        this.weather = weather;
    }

    public Date getDate() {
        return date;
    }

    public double getLowTemp() {
        return lowTemp;
    }

    public double getHighTemp() {
        return highTemp;
    }
    public double getLowTempF() {
        return lowTemp * 1.8 + 32;
    }

    public double getHighTempF() {
        return highTemp * 1.8 + 32;
    }

    public String getWeather() {
        return weather;
    }

    @Override
    public String toString() {
        return "Forecast{" +
                "date=" + date +
                ", lowTemp=" + lowTemp +
                ", highTemp=" + highTemp +
                ", weather='" + weather + '\'' +
                '}';
    }
}
