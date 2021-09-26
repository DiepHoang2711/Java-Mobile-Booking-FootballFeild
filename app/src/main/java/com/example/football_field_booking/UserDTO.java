package com.example.football_field_booking;

public class UserDTO {
    private String userID;
    private String email;
    private String username;
    private String password;
    private String phone;
    private String role;
    private String status;
    private String imageURL;

    public UserDTO(String userID, String email, String username, String phone, String role, String status, String imageURL) {
        this.userID = userID;
        this.email = email;
        this.username = username;
        this.phone = phone;
        this.role = role;
        this.status = status;
        this.imageURL = imageURL;
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

    public void setPassword(String password) {
        this.password = password;
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

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
