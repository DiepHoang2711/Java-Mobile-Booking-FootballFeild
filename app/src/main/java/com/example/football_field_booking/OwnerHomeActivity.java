package com.example.football_field_booking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.football_field_booking.adapters.FootballFieldAdapter;
import com.example.football_field_booking.dtos.FootballFieldDTO;

import java.util.List;

public class OwnerHomeActivity extends AppCompatActivity {

    private Button btnCreate;
    private ListView lvFootballFieldOwner;
    private FootballFieldAdapter fieldAdapter;
    private List<FootballFieldDTO> fieldDTOList;

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

        lvFootballFieldOwner=findViewById(R.id.lvFootballFieldOwner);


    }

    private void loadData(){


//        fieldAdapter=new FootballFieldAdapter(OwnerHomeActivity.this,)
    }
}