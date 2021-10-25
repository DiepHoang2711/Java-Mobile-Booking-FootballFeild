package com.example.football_field_booking.dtos;

import java.io.Serializable;

public class BookingDTO implements Serializable {
    private String bookingID;
    private String userID;
    private String bookingDate;
    private float total;
    private String status;

    public BookingDTO() {
    }

    public BookingDTO(String userID, String bookingDate, float total, String status) {
        this.bookingID = bookingID;
        this.userID = userID;
        this.bookingDate = bookingDate;
        this.total = total;
        this.status = status;
    }

    public BookingDTO(String userID, String bookingDate, float total) {
        this.userID = userID;
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
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    @Override
    public String toString() {
        return "BookingDTO{" +
                "bookingID='" + bookingID + '\'' +
                ", userID='" + userID + '\'' +
                ", bookingDate='" + bookingDate + '\'' +
                ", total=" + total +
                ", status='" + status + '\'' +
                '}';
    }
}
