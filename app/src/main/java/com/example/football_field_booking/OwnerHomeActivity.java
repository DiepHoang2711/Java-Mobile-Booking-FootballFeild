package com.example.football_field_booking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OwnerHomeActivity extends AppCompatActivity {

    private Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_home);
        btnCreate=findViewById(R.id.btnCreateFootballField);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(OwnerHomeActivity.this,CreateFootballFieldActivity.class);
                startActivity(intent);
            }
        });
    }
}