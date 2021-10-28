package com.example.football_field_booking.dtos;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

public class FootballFieldDTO implements Serializable {
    private String fieldID;
    private String name;
    private String location;
    private GeoPoint geoPoint;
    private String geoHash;
    private String type;
    private String image;
    private double rate;
    private String status;


    public FootballFieldDTO() {
    }

    public String getGeoHash() {
        return geoHash;
    }

    public void setGeoHash(String geoHash) {
        this.geoHash = geoHash;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFieldID() {
        return fieldID;
    }

    public void setFieldID(String fieldID) {
        this.fieldID = fieldID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "FootballFieldDTO{" +
                "fieldID='" + fieldID + '\'' +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", type='" + type + '\'' +
                ", image='" + image + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
