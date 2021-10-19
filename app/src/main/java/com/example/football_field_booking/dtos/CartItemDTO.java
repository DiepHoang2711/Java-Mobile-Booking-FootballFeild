package com.example.football_field_booking.dtos;

import java.io.Serializable;
import java.util.List;

public class CartItemDTO implements Serializable {
    private String fieldID;
    private String name;
    private String location;
    private String type;
    private String image;
    private String date;
    private float total;
    private List<TimePickerDTO> listTimeSlot;

    public CartItemDTO() {
    }

    public CartItemDTO(String fieldID, String name, String location, String type, String image, String date, float total, List<TimePickerDTO> timePickerDTOList) {
        this.fieldID = fieldID;
        this.name = name;
        this.location = location;
        this.type = type;
        this.image = image;
        this.date = date;
        this.total = total;
        this.listTimeSlot = timePickerDTOList;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public List<TimePickerDTO> getListTimeSlot() {
        return listTimeSlot;
    }

    public void setListTimeSlot(List<TimePickerDTO> listTimeSlot) {
        this.listTimeSlot = listTimeSlot;
    }

    @Override
    public String toString() {
        return "CartItemDTO{" +
                "fieldID='" + fieldID + '\'' +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", type='" + type + '\'' +
                ", image='" + image + '\'' +
                ", date='" + date + '\'' +
                ", total=" + total +
                ", listTimeSlot=" + listTimeSlot +
                '}';
    }
}
