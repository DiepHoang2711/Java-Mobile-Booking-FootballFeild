package com.example.football_field_booking.dtos;

import java.io.Serializable;
import java.util.List;

public class FootballFieldDocument implements Serializable {
    private FootballFieldDTO fieldInfo;
    private UserDTO ownerInfo;
    private List<TimePickerDTO> timePicker;

    public FootballFieldDocument() {
    }

    public FootballFieldDTO getFieldInfo() {
        return fieldInfo;
    }

    public void setFieldInfo(FootballFieldDTO fieldInfo) {
        this.fieldInfo = fieldInfo;
    }

    public List<TimePickerDTO> getTimePicker() {
        return timePicker;
    }

    public void setTimePicker(List<TimePickerDTO> timePicker) {
        this.timePicker = timePicker;
    }

    public UserDTO getOwnerInfo() {
        return ownerInfo;
    }

    public void setOwnerInfo(UserDTO ownerInfo) {
        this.ownerInfo = ownerInfo;
    }
}
