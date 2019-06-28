package com.example.quakereport;
public class Earthquake {
    private float mag;
    private String location;
    private String date;
    private String place;
    private String time;
    private String link;

    public Earthquake(float mag, String place, String location, String date, String time,String link) {
        this.mag = mag;
        this.location = location;
        this.date = date;
        this.place = place;
        this.time = time;
        this.link=link;
    }

    public float getMag() {
        return mag;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getPlace() {
        return place;
    }

    public String getTime() {
        return time;
    }

    public String getLink() {
        return link;
    }
}
