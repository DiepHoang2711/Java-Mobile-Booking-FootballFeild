package com.example.football_field_booking.dtos;

import androidx.annotation.Nullable;

public class TimePickerDTO {
    private int start;
    private int end;
    private float price;

    public TimePickerDTO() {
    }

    public TimePickerDTO(int start, int end, float price) {
        this.start = start;
        this.end = end;
        this.price = price;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof TimePickerDTO) {
            TimePickerDTO timePickerDTO = (TimePickerDTO) obj;
            return start == timePickerDTO.getStart() && end == timePickerDTO.getEnd();
        }else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "TimePickerDTO{" +
                "start=" + start +
                ", end=" + end +
                ", price=" + price + '\'' +
                '}';
    }
}
