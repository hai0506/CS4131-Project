package com.example.grades.Models;

import java.io.Serializable;

public class Coordinate implements Serializable  {
    private double lat;
    private double lon;
    private static final double R = 6371;

    public Coordinate(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
    public String toString() {
        return lat + ", " + lon;
    }
    public double calcDist(Coordinate coor2) {
        double latDif = Math.toRadians(this.lat) - Math.toRadians(coor2.lat);
        double lonDif = Math.toRadians(this.lon) - Math.toRadians(coor2.lon);
        double a = Math.pow(Math.sin(latDif/2),2) + Math.cos(this.lat)*Math.cos(coor2.lat)* Math.pow(Math.sin(lonDif/2),2);
        double c = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R*c;
        return d;
    }
}
