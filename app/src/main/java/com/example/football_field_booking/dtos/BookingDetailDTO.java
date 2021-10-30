package com.example.football_field_booking.dtos;

import java.io.Serializable;
import java.util.List;

public class BookingDetailDTO implements Serializable {
    private String ID;
    private UserDTO userInfo;
    private FootballFieldDTO fieldInfo;
    private String date;
    private float total;
    private List<TimePickerDTO> timePicker;
    private String fieldAndDate;
    private boolean alreadyRating;

    public BookingDetailDTO() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public UserDTO getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserDTO userInfo) {
        this.userInfo = userInfo;
    }

    public FootballFieldDTO getFieldInfo() {
        return fieldInfo;
    }

    public void setFieldInfo(FootballFieldDTO fieldInfo) {
        this.fieldInfo = fieldInfo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public List<TimePickerDTO> getTimePicker() {
        return timePicker;
    }

    public void setTimePicker(List<TimePickerDTO> timePicker) {
        this.timePicker = timePicker;
    }

    public String getFieldAndDate() {
        return fieldAndDate;
    }

    public void setFieldAndDate(String fieldAndDate) {
        this.fieldAndDate = fieldAndDate;
    }

    public boolean isAlreadyRating() {
        return alreadyRating;
    }

    public void setAlreadyRating(boolean alreadyRating) {
        this.alreadyRating = alreadyRating;
    }
}
