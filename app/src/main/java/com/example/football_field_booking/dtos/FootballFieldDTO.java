package com.example.football_field_booking.dtos;

import java.io.Serializable;

public class FootballFieldDTO implements Serializable {
    private String name;
    private String location;
    private String type;
    private String fieldID;
    private String image;
    private boolean status;


    public FootballFieldDTO() {
    }

    public FootballFieldDTO(String name, String location, String type, String fieldID, String image, boolean status) {
        this.name = name;
        this.location = location;
        this.type = type;
        this.fieldID = fieldID;
        this.image = image;
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
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
}
