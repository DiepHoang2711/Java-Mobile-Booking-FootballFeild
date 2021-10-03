package com.example.football_field_booking.dtos;

public class FootballFieldDTO {
    private String fieldID;
    private String name;
    private String location;
    private String type;

    public FootballFieldDTO() {
    }


    public FootballFieldDTO(String name, String location, String type) {
        this.fieldID=fieldID;
        this.name = name;
        this.location = location;
        this.type = type;

    }

    public String getFieldID() {
        return fieldID;
    }

    public void setFieldID(String fieldID) {
        this.fieldID = fieldID;
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
