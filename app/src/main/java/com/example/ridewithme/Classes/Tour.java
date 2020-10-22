package com.example.ridewithme.Classes;

import com.google.type.LatLng;

import java.util.Date;

public class Tour {

    private  String date ;
    private int time_in_minutes;
    private String source;
    private String dest;
    private double avg_speed;
    private String km;



    public Tour(String date, int time_in_minutes, String source, String dest, double avg_speed, String km) {
        this.date = date;
        this.time_in_minutes = time_in_minutes;
        this.source = source;
        this.dest = dest;
        this.avg_speed = avg_speed;
        this.km = km;
    }

    public Tour() {
    }


    public Tour setAvg_speed(double avg_speed) {
        this.avg_speed = avg_speed;
        return this;
    }
    public String getDate() {
        return date;
    }

    public Tour setDate(String date) {
        this.date = date;
        return this;
    }
    public String getKm() {
        return km;
    }

    public Tour setKm(String km) {
        this.km = km;
        return this;
    }

    public int getTime_in_minutes() {
        return time_in_minutes;
    }



    public String getSource() {
        return source;
    }

    public String getDest() {
        return dest;
    }

    public double getAvg_speed() {
        return avg_speed;
    }

    public Tour setTime_in_minutes(int time_in_minutes) {
        this.time_in_minutes = time_in_minutes;
        return this;
    }

    public Tour setSource(String source) {
        this.source = source;
        return this;
    }

    public Tour setDest(String dest) {
        this.dest = dest;
        return this;
    }

    public Tour addAvg_speed(double avg_speed) {
        this.avg_speed += avg_speed;
        return this;
    }



}
