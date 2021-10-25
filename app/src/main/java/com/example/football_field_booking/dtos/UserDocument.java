package com.example.football_field_booking.dtos;

import java.io.Serializable;
import java.util.List;

public class UserDocument implements Serializable {
    private UserDTO userInfo;
    private List<FootballFieldDTO> fieldsInfo;
    private List<String> tokens;

    public UserDocument() {
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    public UserDTO getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserDTO userInfo) {
        this.userInfo = userInfo;
    }

    public List<FootballFieldDTO> getFieldsInfo() {
        return fieldsInfo;
    }

    public void setFieldsInfo(List<FootballFieldDTO> fieldsInfo) {
        this.fieldsInfo = fieldsInfo;
    }
}
