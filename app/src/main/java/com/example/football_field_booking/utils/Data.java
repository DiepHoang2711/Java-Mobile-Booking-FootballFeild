package com.example.football_field_booking.utils;

public class Data {

    String body,title,fieldID;

    public Data(String body, String title, String fieldID) {
        this.body = body;
        this.title = title;
        this.fieldID = fieldID;
    }

    public String getFieldID() {
        return fieldID;
    }

    public void setFieldID(String fieldID) {
        this.fieldID = fieldID;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
