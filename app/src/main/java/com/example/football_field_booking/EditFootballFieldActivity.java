package com.example.football_field_booking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
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
import com.example.football_field_booking.dtos.FootballFieldDocument;
import com.example.football_field_booking.dtos.TimePickerDTO;
import com.example.football_field_booking.utils.Utils;
import com.example.football_field_booking.validations.Validation;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditFootballFieldActivity extends AppCompatActivity {

    public static final int RC_IMAGE_PICKER = 1000;
    public static final int RC_LOCATION = 1002;

    private Button btnChooseImg, btnUpdateField, btnDeleteField, btnLocation;
    private ImageButton imgBtnAdd;
    private ImageView imgFootBallField;
    private TextInputLayout tlFootballFieldName, tlLocation, tlType, tlStatus;
    private Uri imgUri;
    private ListView lvTimePickerWorking;
    private AutoCompleteTextView auComTxtType, auComTxtStatus;
    private FootballFieldDTO fieldDTO, fieldOldDTO;
    private TimePickerAdapter timePickerAdapter;
    private GeoPoint geoPoint;
    private String geoHash;
    private Utils utils;
    private Validation val;
    public static final String STATUS_INACTIVE = "inactive";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_football_field);

        tlFootballFieldName = findViewById(R.id.tlFootballFieldName);
        tlLocation = findViewById(R.id.tlLocation);
        tlType = findViewById(R.id.tlType);
        tlStatus = findViewById(R.id.tlStatus);
        btnChooseImg = findViewById(R.id.btnChooseImage);
        btnUpdateField = findViewById(R.id.btnUpdateField);
        btnLocation = findViewById(R.id.btnLocation);
        imgBtnAdd = findViewById(R.id.imgBtnAdd);
        imgFootBallField = findViewById(R.id.imgFootBallField);
        auComTxtType = findViewById(R.id.auComTxtType);
        auComTxtStatus = findViewById(R.id.auComTxtStatus);
        lvTimePickerWorking = findViewById(R.id.lvTimePickerWorking);
        btnDeleteField = findViewById(R.id.btnDeleteField);
        utils = new Utils();
        val = new Validation();

        Intent intent = this.getIntent();
        String fieldID = intent.getStringExtra("fieldID");
        if (fieldID == null) {
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
                            ArrayAdapter<String> adapterType = new ArrayAdapter<String>(EditFootballFieldActivity.this,
                                    android.R.layout.simple_spinner_item, listTypeField);
                            ArrayAdapter<String> adapterStatus = new ArrayAdapter<String>(EditFootballFieldActivity.this,
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
                if (task.isSuccessful()) {
                    try {
                        DocumentSnapshot doc = task.getResult();
                        fieldDTO = doc.get("fieldInfo", FootballFieldDTO.class);
                        fieldOldDTO = doc.get("fieldInfo", FootballFieldDTO.class);
                        Log.d("USER", "dto: " + fieldDTO);

                        geoPoint = fieldDTO.getGeoPoint();
                        geoHash = fieldDTO.getGeoHash();
                        tlFootballFieldName.getEditText().setText(fieldDTO.getName());
                        tlLocation.getEditText().setText(fieldDTO.getLocation());
                        auComTxtType.setText(fieldDTO.getType(), false);
                        auComTxtStatus.setText(fieldDTO.getStatus(), false);

                        try {
                            Uri uri = Uri.parse(fieldDTO.getImage());
                            Glide.with(imgFootBallField.getContext())
                                    .load(uri)
                                    .into(imgFootBallField);
                        } catch (Exception e) {
                            imgFootBallField.setImageResource(R.drawable.myfield);
                        }

                        List<TimePickerDTO> timePickerDTOList = doc.toObject(FootballFieldDocument.class).getTimePicker();
                        timePickerAdapter = new TimePickerAdapter(EditFootballFieldActivity.this, timePickerDTOList);
                        lvTimePickerWorking.setAdapter(timePickerAdapter);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(EditFootballFieldActivity.this, "Get data fail", Toast.LENGTH_SHORT).show();
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

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditFootballFieldActivity.this, GoogleMapActivity.class);
                intent.putExtra("action", "chooseLocation");
                startActivityForResult(intent, RC_LOCATION);
            }
        });

        btnUpdateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (fieldDTO != null) {
                        String fieldName = tlFootballFieldName.getEditText().getText().toString();
                        String location = tlLocation.getEditText().getText().toString();
                        String type = auComTxtType.getText().toString();
                        String status = auComTxtStatus.getText().toString();

                        if (isValidUpdate(fieldName, location, type, status) && timePickerAdapter.isValidTimePicker()) {
                            fieldDTO.setName(fieldName);
                            fieldDTO.setLocation(location);
                            fieldDTO.setGeoPoint(geoPoint);
                            fieldDTO.setGeoHash(geoHash);
                            fieldDTO.setType(type);
                            fieldDTO.setStatus(status);

                            ProgressDialog progressDialog = new ProgressDialog(EditFootballFieldActivity.this);
                            utils.showProgressDialog(progressDialog, "Updating ....", "Please wait for update field");
                            updateFootballField();
                            progressDialog.cancel();

                        }
                    }
                } catch (Exception e) {
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

        btnDeleteField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(EditFootballFieldActivity.this);
                alert.setTitle("Delete entry");
                alert.setMessage("Are you sure you want to delete?");
                alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       fieldDTO.setStatus(STATUS_INACTIVE);
                        updateFootballField();
                    }
                });
                alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // close dialog
                        dialog.cancel();
                    }
                });
                alert.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_IMAGE_PICKER) {
            if (resultCode == RESULT_OK) {
                try {
                    imgUri = data.getData();
                    imgFootBallField.setImageURI(imgUri);
                    ProgressDialog progressDialog = new ProgressDialog(EditFootballFieldActivity.this);
                    utils.showProgressDialog(progressDialog, "Uploading ....", "Please wait for uploading image");
                    uploadImgFootballField(imgUri);
                    progressDialog.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }else if(requestCode == RC_LOCATION && resultCode == RESULT_OK) {
            try {
                double lat = data.getDoubleExtra("lat", 0);
                double lng = data.getDoubleExtra("lng", 0);
                if(lat != 0 && lng != 0) {
                    geoPoint = new GeoPoint(lat, lng);
                    geoHash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(lat, lng));
                }
                String location = data.getStringExtra("locationName");
                tlLocation.getEditText().setText(location);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateFootballField() {
        try {
            FootballFieldDAO fieldDAO = new FootballFieldDAO();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            List<TimePickerDTO> list = timePickerAdapter.getTimePickerDTOList();
            fieldDAO.updateFootballField(fieldDTO ,fieldOldDTO, user.getUid(), list).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditFootballFieldActivity.this, "Update field success",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditFootballFieldActivity.this, "Update field fail",
                                Toast.LENGTH_SHORT).show();
                        Log.d("USER", "errorrr: " + task.getException());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadImgFootballField(Uri uri) {
        try {
            FootballFieldDAO fieldDAO = new FootballFieldDAO();
            fieldDAO.uploadImgFootballFieldToFirebase(uri).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri uriRes = task.getResult();
                        fieldDTO.setImage(uriRes.toString());
                        updateFootballField();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean isValidUpdate(String name, String location, String type, String status) {
        boolean result = true;
        utils.clearError(tlFootballFieldName);
        utils.clearError(tlLocation);
        utils.clearError(tlType);
        utils.clearError(tlStatus);

        if (val.isEmpty(status)) {
            utils.showError(tlStatus, "Status must not be blank");
            result = false;
        }
        if (val.isEmpty(type)) {
            utils.showError(tlType, "Type must not be blank");
            result = false;
        }
        if (val.isEmpty(location)) {
            utils.showError(tlLocation, "Location must not be blank");
            result = false;
        }
        if (val.isEmpty(name)) {
            utils.showError(tlFootballFieldName, "Name must not be blank");
            result = false;
        }
        if(geoPoint == null) {
            Toast.makeText(this, "ERROR: Please choose geo point", Toast.LENGTH_SHORT).show();
            result = false;
        }
        return result;
    }
}