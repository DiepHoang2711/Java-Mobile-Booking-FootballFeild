package com.example.football_field_booking.dtos;

import java.io.Serializable;
import java.util.List;

public class CartItemDTO implements Serializable {
    private String cartItemID;
    private UserDTO userInfo;
    private FootballFieldDTO fieldInfo;
    private String date;
    private float total;
    private List<TimePickerDTO> timePicker;
    private String fieldAndDate;

    public CartItemDTO() {
    }

    public String getCartItemID() {
        return cartItemID;
    }

    public CartItemDTO(UserDTO userInfo, FootballFieldDTO fieldInfo, String date, float total, List<TimePickerDTO> timePicker) {
        this.userInfo = userInfo;
        this.fieldInfo = fieldInfo;
        this.date = date;
        this.total = total;
        this.timePicker = timePicker;
    }

    public void setCartItemID(String cartItemID) {
        this.cartItemID = cartItemID;
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

    @Override
    public String toString() {
        return "CartItemDTO{" +
                "cartItemID='" + cartItemID + '\'' +
                ", userInfo=" + userInfo +
                ", fieldInfo=" + fieldInfo +
                ", date='" + date + '\'' +
                ", total=" + total +
                ", timePicker=" + timePicker +
                '}';
    }
}
