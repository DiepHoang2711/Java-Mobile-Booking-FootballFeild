package com.example.football_field_booking.dtos;

import java.io.Serializable;

public class RatingDTO implements Serializable {

    private String ratingID;
    private UserDTO userInfo;
    private FootballFieldDTO fieldInfo;
    private String comment;
    private float rating;
    private String date;

    public RatingDTO() {
    }

    public RatingDTO(UserDTO userInfo, FootballFieldDTO fieldInfo, String comment, float rating, String date) {
        this.userInfo = userInfo;
        this.fieldInfo = fieldInfo;
        this.comment = comment;
        this.rating = rating;
        this.date = date;
    }

    public String getRatingID() {
        return ratingID;
    }

    public void setRatingID(String ratingID) {
        this.ratingID = ratingID;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
