package com.example.grades.Models;

import java.io.Serializable;
import java.util.Arrays;

public class Station implements Serializable {
    private String id;
    private double temperature;
    private double humidity;
    private double windSpeed = 999;
    private String region;
    private String weather;
    private String name;
    private Coordinate coordinate;
    private Pollutants psi;
    private long uv;

    public Station(String name, String id, double temperature, double longitude, double latitude) {
        this.name = name;
        this.id = id;
        this.temperature = temperature;
        this.coordinate = new Coordinate(latitude,longitude);
        double distFromN = this.getCoordinate().calcDist(new Coordinate(1.41803,103.82));
        double distFromS = this.getCoordinate().calcDist(new Coordinate(1.29587,103.82));
        double distFromW = this.getCoordinate().calcDist(new Coordinate(1.35735,103.7));
        double distFromE = this.getCoordinate().calcDist(new Coordinate(1.35735,103.94));
        double distFromC = this.getCoordinate().calcDist(new Coordinate(1.35735,103.82));
        double[] dist = {distFromN,distFromS,distFromC,distFromE,distFromW};
        Arrays.sort(dist);
        if(dist[0] == distFromC) region = "central";
        if(dist[0] == distFromN) region = "north";
        if(dist[0] == distFromS) region = "south";
        if(dist[0] == distFromW) region = "west";
        if(dist[0] == distFromE) region = "east";
    }

    public String getId() {
        return id;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getWindSpeed() {
        return windSpeed;
    }
    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }


    public String getRegion() {
        return region;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public double getFahrenheit() {
        return temperature * 1.8 + 32;
    }

    public double getWindSpeedkmh() {
        return windSpeed * 3.6;
    }

    public double getWindSpeedfts() {
        return windSpeed * 3.28;
    }

    public double getWindSpeedmih() {
        return windSpeed * 2.237;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public String getName() {
        return name;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public Pollutants getPsi() {
        return psi;
    }

    public void setPsi(Pollutants psi) {
        this.psi = psi;
    }

    public long getUv() {
        return uv;
    }

    public void setUv(long uv) {
        this.uv = uv;
    }

    @Override
    public String toString() {
        return "Station{" +
                "id='" + id + '\'' +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", windSpeed=" + windSpeed +
                ", region='" + region + '\'' +
                ", weather='" + weather + '\'' +
                ", name='" + name + '\'' +
                ", coordinate=" + coordinate +
                ", psi=" + psi +
                ", uv=" + uv +
                '}';
    }
}
