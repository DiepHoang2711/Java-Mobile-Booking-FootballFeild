package com.example.football_field_booking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.football_field_booking.adapters.TimePickerAdapter;
import com.example.football_field_booking.daos.FootballFieldDAO;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.example.football_field_booking.dtos.TimePickerDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UpdateFootballFieldActivity extends AppCompatActivity {

    public static final int RC_IMAGE_PICKER = 1000;

    private Button btnChooseImg;
    private ImageView imgFootBallField;
    private TextInputLayout tlFootballFieldName, tlLocation, tlType, tlStatus;
    private Uri imgUri;
    private ListView lvTimePickerWorking;
    private AutoCompleteTextView auComTxtType, auComTxtStatus;
    private FootballFieldDTO fieldDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_football_field);

        tlFootballFieldName = findViewById(R.id.tlFootballFieldName);
        tlLocation = findViewById(R.id.tlLocation);
        tlType = findViewById(R.id.tlType);
        tlStatus = findViewById(R.id.tlStatus);
        btnChooseImg = findViewById(R.id.btnChooseImage);
        imgFootBallField = findViewById(R.id.imgFootBallField);
        auComTxtType = findViewById(R.id.auComTxtType);
        auComTxtStatus = findViewById(R.id.auComTxtStatus);
        lvTimePickerWorking = findViewById(R.id.lvTimePickerWorking);

        Intent intent = this.getIntent();
        String fieldID = intent.getStringExtra("fieldID");
        if(fieldID == null) {
            Toast.makeText(this, getResources().getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        //load list type tu database
        FootballFieldDAO footballFieldDAO = new FootballFieldDAO();
        try {
            footballFieldDAO.getTypeOfFootballField()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            List<String> listTypeField = (ArrayList<String>) documentSnapshot.get("typeFootBallField");
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(UpdateFootballFieldActivity.this,
                                    android.R.layout.simple_spinner_item, listTypeField);
                            auComTxtType.setAdapter(adapter);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

        footballFieldDAO.getFieldByID(fieldID).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    try {
                        DocumentSnapshot doc = task.getResult();
                        fieldDTO = doc.toObject(FootballFieldDTO.class);
                        Log.d("USER", "dto: " + fieldDTO);

                        tlFootballFieldName.getEditText().setText(fieldDTO.getName());
                        tlLocation.getEditText().setText(fieldDTO.getLocation());
                        auComTxtType.setText(fieldDTO.getType(), false);
                        auComTxtType.setText(fieldDTO.getStatus(), false);

                        try {
                            Uri uri = Uri.parse(fieldDTO.getImage());
                            Glide.with(imgFootBallField.getContext())
                                    .load(uri)
                                    .into(imgFootBallField);
                        }catch (Exception e) {
                            imgFootBallField.setImageResource(R.drawable.myfield);
                        }

                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(UpdateFootballFieldActivity.this, "Get data fail", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        footballFieldDAO.getAllTimePickerOfField(fieldID).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    try {
                        List<TimePickerDTO> list = new ArrayList<>();
                        for (QueryDocumentSnapshot doc: task.getResult()) {
                            TimePickerDTO dto = doc.toObject(TimePickerDTO.class);
                            list.add(dto);
                        }
                        TimePickerAdapter timePickerAdapter = new TimePickerAdapter(UpdateFootballFieldActivity.this);
                        timePickerAdapter.setTimePickerDTOList(list);
                        lvTimePickerWorking.setAdapter(timePickerAdapter);
                        
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                }else {
                    Toast.makeText(UpdateFootballFieldActivity.this, "Get time picker data fail",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        btnChooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, RC_IMAGE_PICKER);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_IMAGE_PICKER) {
            if(resultCode == RESULT_OK) {
                imgUri = data.getData();
                imgFootBallField.setImageURI(imgUri);
                Log.d("USER", "Success");
            }
        }
    }
}