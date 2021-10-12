package com.example.football_field_booking;

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

import com.example.football_field_booking.daos.FootballFieldDAO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UpdateFootballFieldActivity extends AppCompatActivity {

    public static final int RC_IMAGE_PICKER = 1000;

    private Button btnChooseImg;
    private ImageView imgFootBallField;
    private Uri imgUri;
    private AutoCompleteTextView auComTxtType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_football_field);

        btnChooseImg = findViewById(R.id.btnChooseImage);
        imgFootBallField = findViewById(R.id.imgFootBallField);
        auComTxtType = findViewById(R.id.auComTxtType);

        btnChooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, RC_IMAGE_PICKER);
            }
        });

        //load list type tu database
        FootballFieldDAO footballFieldDAO = new FootballFieldDAO();
        try {
            footballFieldDAO.getTypeOfFootballField()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            List<String> listTypeField = (ArrayList<String>) documentSnapshot.get("typeFootBallField");
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(UpdateFootballFieldActivity.this, R.layout.item_type_football_field, listTypeField);
                            auComTxtType.setAdapter(adapter);
                            auComTxtType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    auComTxtType.setText(adapterView.getItemAtPosition(i).toString());
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                }
                            });
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

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