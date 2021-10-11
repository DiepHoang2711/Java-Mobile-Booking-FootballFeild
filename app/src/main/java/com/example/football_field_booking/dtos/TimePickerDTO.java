package com.example.football_field_booking.dtos;

public class TimePickerDTO {
    private int start;
    private int end;
    private float price;
    private String timePickerID;

    public TimePickerDTO() {
    }

    public TimePickerDTO(int start, int end, float price, String timePickerID) {
        this.start = start;
        this.end = end;
        this.price = price;
        this.timePickerID = timePickerID;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getTimePickerID() {
        return timePickerID;
    }

    public void setTimePickerID(String timePickerID) {
        this.timePickerID = timePickerID;
    }
}
