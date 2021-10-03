package com.example.football_field_booking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.example.football_field_booking.adapters.FieldAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OwnerHomeActivity extends AppCompatActivity {

    private Button btnCreate;
    private RecyclerView rvListFields;
    private FieldAdapter fieldAdapter;

    private FirebaseFirestore db;

    List<FootballFieldDTO> fieldDTOList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_home);
        btnCreate = findViewById(R.id.btnCreateFootballField);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OwnerHomeActivity.this, CreateFootballFieldActivity.class);
                startActivity(intent);
            }
        });

        rvListFields = findViewById(R.id.rvListFields);
        fieldAdapter = new FieldAdapter(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvListFields.setLayoutManager(linearLayoutManager);

       showData();

    }

    private void showData() {
        db = FirebaseFirestore.getInstance();
        try {
            db.collection("footballFields")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                fieldDTOList = new ArrayList<>();
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    FootballFieldDTO fieldDTO = new FootballFieldDTO();
                                    fieldDTO.setFieldID(doc.getString("fieldID"));
                                    fieldDTO.setName(doc.getString("name"));
                                    fieldDTO.setLocation(doc.getString("location"));
                                    fieldDTO.setType(doc.getString("type"));
                                    fieldDTOList.add(fieldDTO);
                                }
                                System.out.println("LOAD DATA SUCCESS");
                                System.out.println("Size Data: "+task.getResult().size());
                                System.out.println("Size: " + fieldDTOList.size());
                                fieldAdapter.setFieldDTOList(fieldDTOList);
                                fieldAdapter = new FieldAdapter(OwnerHomeActivity.this, fieldDTOList);
                                rvListFields.setAdapter(fieldAdapter);
                            } else {
                                System.out.println("Error: Eo load dc");
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}