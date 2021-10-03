package com.example.football_field_booking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class OwnerFootballFieldDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onwer_football_field_detail);
        Intent intent=this.getIntent();
        String fieldID=intent.getStringExtra("fieldID");

    }
}