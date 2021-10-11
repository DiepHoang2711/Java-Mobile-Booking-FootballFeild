package com.example.football_field_booking;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class OwnerFootballFieldDetailActivity extends AppCompatActivity {

    private TextView txtSelectDate;
    private DatePickerDialog datePickerDialog;
    private int year, month, day;
    private Calendar calendar;
    String now;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onwer_football_field_detail);
//        Intent intent=this.getIntent();
//        String fieldID=intent.getStringExtra("fieldID");
        calendar = Calendar.getInstance();
        txtSelectDate = findViewById(R.id.txtSelectDate);

        now = calendar.get(Calendar.DAY_OF_MONTH) +
                "/" + (calendar.get(Calendar.MONTH) + 1) +
                "/" + calendar.get(Calendar.YEAR);
        txtSelectDate.setText(now);
        System.out.println("Now:"+now);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
    }

    public void clickToChangeDate(View view) {
        String[] selectedDate = txtSelectDate.getText().toString().split("/");
        if(!txtSelectDate.getText().equals(now)){
            day = Integer.parseInt(selectedDate[0]);
            month = Integer.parseInt(selectedDate[1])-1;
            year = Integer.parseInt(selectedDate[2]);
        }
        System.out.println(day + "/" + month + "/" + year);
        datePickerDialog = new DatePickerDialog(OwnerFootballFieldDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                txtSelectDate.setText(d + "/" + (m + 1) + "/" + y);
            }
        }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }
}