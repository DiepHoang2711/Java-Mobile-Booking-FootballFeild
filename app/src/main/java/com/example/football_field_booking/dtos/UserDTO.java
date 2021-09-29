package com.example.football_field_booking.dtos;

public class UserDTO {
    private String userID;
    private String email;
    private String username;
    private String phone;
    private String role;
    private String status;
    private String photoUri;

    public UserDTO() {
    }

    public UserDTO(String userID, String email, String username, String phone, String role, String status, String photoUri) {
        this.userID = userID;
        this.email = email;
        this.username = username;
        this.phone = phone;
        this.role = role;
        this.status = status;
        this.photoUri = photoUri;
    }

    public UserDTO(String userID, String email, String username, String role, String status) {
        this.userID = userID;
        this.email = email;
        this.username = username;
        this.role = role;
        this.status = status;
    }

    public String getUserID() {
        return userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
}
