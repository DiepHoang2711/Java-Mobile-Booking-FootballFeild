package com.example.football_field_booking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.football_field_booking.adapters.TimePickerAdapter;
import com.example.football_field_booking.daos.FootballFieldDAO;
import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.example.football_field_booking.dtos.TimePickerDTO;
import com.example.football_field_booking.utils.Utils;
import com.example.football_field_booking.validations.Validation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UpdateFootballFieldActivity extends AppCompatActivity {

    public static final int RC_IMAGE_PICKER = 1000;

    private Button btnChooseImg, btnUpdateField;
    private ImageButton imgBtnAdd;
    private ImageView imgFootBallField;
    private TextInputLayout tlFootballFieldName, tlLocation, tlType, tlStatus;
    private Uri imgUri;
    private ListView lvTimePickerWorking;
    private AutoCompleteTextView auComTxtType, auComTxtStatus;
    private FootballFieldDTO fieldDTO;
    private TimePickerAdapter timePickerAdapter;
    private Utils utils;
    private Validation val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_football_field);

        tlFootballFieldName = findViewById(R.id.tlFootballFieldName);
        tlLocation = findViewById(R.id.tlLocation);
        tlType = findViewById(R.id.tlType);
        tlStatus = findViewById(R.id.tlStatus);
        btnChooseImg = findViewById(R.id.btnChooseImage);
        btnUpdateField = findViewById(R.id.btnUpdateField);
        imgBtnAdd = findViewById(R.id.imgBtnAdd);
        imgFootBallField = findViewById(R.id.imgFootBallField);
        auComTxtType = findViewById(R.id.auComTxtType);
        auComTxtStatus = findViewById(R.id.auComTxtStatus);
        lvTimePickerWorking = findViewById(R.id.lvTimePickerWorking);
        utils = new Utils();
        val = new Validation();

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
            footballFieldDAO.getConstOfFootballField()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            List<String> listTypeField = (ArrayList<String>) documentSnapshot.get("typeFootballField");
                            List<String> listStatusField = (ArrayList<String>) documentSnapshot.get("status");
                            ArrayAdapter<String> adapterType = new ArrayAdapter<String>(UpdateFootballFieldActivity.this,
                                    android.R.layout.simple_spinner_item, listTypeField);
                            ArrayAdapter<String> adapterStatus = new ArrayAdapter<String>(UpdateFootballFieldActivity.this,
                                    android.R.layout.simple_spinner_item, listStatusField);
                            auComTxtType.setAdapter(adapterType);
                            auComTxtStatus.setAdapter(adapterStatus);
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
                        auComTxtStatus.setText(fieldDTO.getStatus(), false);

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
                        timePickerAdapter = new TimePickerAdapter(UpdateFootballFieldActivity.this, list);
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

        btnUpdateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(fieldDTO != null) {
                        String fieldName = tlFootballFieldName.getEditText().getText().toString();
                        String location = tlLocation.getEditText().getText().toString();
                        String type = auComTxtType.getText().toString();
                        String status = auComTxtStatus.getText().toString();

                        if(isValidUpdate(fieldName, location, type, status) && timePickerAdapter.isValidTimePicker()) {
                            fieldDTO.setName(fieldName);
                            fieldDTO.setLocation(location);
                            fieldDTO.setType(type);
                            fieldDTO.setStatus(status);

                            List<TimePickerDTO> list = timePickerAdapter.getTimePickerDTOList();
                            ProgressDialog progressDialog = new ProgressDialog(UpdateFootballFieldActivity.this);
                            utils.showProgressDialog(progressDialog, "Updating ....", "Please wait for update field");
                            updateFootballField();
                            updateTimePicker(list, fieldID);
                            progressDialog.cancel();

                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        imgBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("timePicker", timePickerAdapter.getTimePickerDTOList().size() + "");
                timePickerAdapter.addNewTimePickerWorking();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_IMAGE_PICKER) {
            if(resultCode == RESULT_OK) {
                try {
                    imgUri = data.getData();
                    imgFootBallField.setImageURI(imgUri);
                    UserDAO userDAO = new UserDAO();
                    ProgressDialog progressDialog = new ProgressDialog(UpdateFootballFieldActivity.this);
                    utils.showProgressDialog(progressDialog, "Uploading ....", "Please wait for uploading image");
                    uploadImgFootballField(imgUri);
                    progressDialog.cancel();
                }catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void updateFootballField () {
        try {
            FootballFieldDAO fieldDAO = new FootballFieldDAO();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            fieldDAO.updateFootballField(fieldDTO,user.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(UpdateFootballFieldActivity.this, "Update field success",
                                Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(UpdateFootballFieldActivity.this, "Update field fail",
                                Toast.LENGTH_SHORT).show();
                        Log.d("USER", "errorrr: " + task.getException());
                    }
                }
            });
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadImgFootballField (Uri uri) {
        try {
            FootballFieldDAO fieldDAO = new FootballFieldDAO();
            fieldDAO.uploadImgFootballFieldToFirebase(uri).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()) {
                        Uri uriRes = task.getResult();
                        fieldDTO.setImage(uriRes.toString());
                        updateFootballField();
                    }
                }
            });
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateTimePicker (List<TimePickerDTO> listDTO, String fieldID) {
        try {
            FootballFieldDAO fieldDAO = new FootballFieldDAO();
            fieldDAO.updateTimePicker(listDTO, fieldID);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isValidUpdate(String name, String location, String type, String status) {
        boolean result = true;
        utils.clearError(tlFootballFieldName);
        utils.clearError(tlLocation);
        utils.clearError(tlType);
        utils.clearError(tlStatus);

        if(val.isEmpty(status)){
            utils.showError(tlStatus, "Status must not be blank");
            result = false;
        }
        if (val.isEmpty(type)) {
            utils.showError(tlType, "Type must not be blank");
            result = false;
        }
        if(val.isEmpty(location)) {
            utils.showError(tlLocation, "Location must not be blank");
            result = false;
        }
        if(val.isEmpty(name)) {
            utils.showError(tlFootballFieldName, "Name must not be blank");
            result = false;
        }
        return result;
    }
}