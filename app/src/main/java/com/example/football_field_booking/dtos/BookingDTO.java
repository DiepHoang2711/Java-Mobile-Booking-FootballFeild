package com.example.football_field_booking.dtos;

import java.io.Serializable;
import java.util.List;

public class BookingDTO implements Serializable {
    private String bookingID;
    private String UserID;
    private String bookingDate;
    private float total;
    private String status;

    public BookingDTO() {
    }

    public BookingDTO(String userID, String bookingDate, float total) {
        UserID = userID;
        this.bookingDate = bookingDate;
        this.total = total;
    }

    public String getBookingID() {
        return bookingID;
    }

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
