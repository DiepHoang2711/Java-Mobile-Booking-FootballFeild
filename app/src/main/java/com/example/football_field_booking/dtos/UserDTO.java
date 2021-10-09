package com.example.football_field_booking.dtos;

import java.io.Serializable;

public class UserDTO implements Serializable {

    private String userID;
    private String email;
    private String fullName;
    private String phone;
    private String role;
    private String status;
    private String photoUri;

    public UserDTO() {
    }

    public UserDTO(String userID, String email, String fullName, String phone, String role, String status, String photoUri) {
        this.userID = userID;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
        this.status = status;
        this.photoUri = photoUri;
    }

    public UserDTO(String userID, String email, String fullName, String role, String status) {
        this.userID = userID;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.status = status;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "userID='" + userID + '\'' +
                ", email='" + email + '\'' +
                ", username='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                ", photoUri='" + photoUri + '\'' +
                '}';
    }
}
