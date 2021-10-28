package com.example.football_field_booking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.example.football_field_booking.adapters.TimePickerAdapter;
import com.example.football_field_booking.daos.FootballFieldDAO;
import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.example.football_field_booking.dtos.TimePickerDTO;
import com.example.football_field_booking.dtos.UserDTO;
import com.example.football_field_booking.utils.Utils;
import com.example.football_field_booking.validations.Validation;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;


public class CreateFootballFieldActivity extends AppCompatActivity {

    public static final int RC_GALLERY = 1000;
    public static final String ACTIVE = "active";
    public static final int RC_LOCATION = 1002;
    private Button btnChooseImg, btnLocation;
    private ImageView imgPhoto;
    private Uri uriImg;
    private TextInputLayout tlName, tlLocation, tlType;
    private AutoCompleteTextView auComTxtType;
    private ImageButton imgBtnAdd;
    private TimePickerAdapter timePickerAdapter;
    private ListView lvTimePickerWorking;
    private List<String> listTypeField;
    private List<TimePickerDTO> list;
    private FootballFieldDTO footballFieldDTO;
    private GeoPoint geoPoint;
    private String geoHash;
    private Utils utils;
    private Validation val;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_football_field);

        tlName = findViewById(R.id.tlFootballFieldName);
        tlLocation = findViewById(R.id.tlLocation);
        tlType = findViewById(R.id.tlType);
        auComTxtType = findViewById(R.id.auComTxtType);
        imgPhoto = findViewById(R.id.img_photo);
        btnChooseImg = findViewById(R.id.btnChooseImage);
        btnLocation = findViewById(R.id.btnLocation);
        imgBtnAdd = findViewById(R.id.imgBtnAdd);
        lvTimePickerWorking = findViewById(R.id.lvTimePickerWorking);
        utils = new Utils();
        val = new Validation();

        btnChooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, RC_GALLERY);
            }
        });

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateFootballFieldActivity.this, GoogleMapActivity.class);
                intent.putExtra("action", "chooseLocation");
                startActivityForResult(intent, RC_LOCATION);
            }
        });

        //load list type tu database
        FootballFieldDAO footballFieldDAO = new FootballFieldDAO();
        try {
            footballFieldDAO.getConstOfFootballField()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            listTypeField = (ArrayList<String>) task.getResult().get("typeFootballField");
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateFootballFieldActivity.this, android.R.layout.simple_spinner_item, listTypeField);
                            auComTxtType.setAdapter(adapter);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        timePickerAdapter = new TimePickerAdapter(CreateFootballFieldActivity.this);
        lvTimePickerWorking.setAdapter(timePickerAdapter);
        timePickerAdapter.addNewTimePickerWorking();
        imgBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("timePicker", timePickerAdapter.getTimePickerDTOList().size() + "");
                timePickerAdapter.addNewTimePickerWorking();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GALLERY) {
            if (resultCode == RESULT_OK) {
                try {
                    uriImg = data.getData();
                    imgPhoto.setImageURI(uriImg);
                    Log.d("USER", "Success");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } else if(requestCode == RC_LOCATION && resultCode == RESULT_OK) {
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

    public void clickToCreate(View view) {
        String name = tlName.getEditText().getText().toString();
        String location = tlLocation.getEditText().getText().toString();
        String type = auComTxtType.getText().toString();

        if (isValidCreate(name, location, type) && timePickerAdapter.isValidTimePicker()) {
            footballFieldDTO = new FootballFieldDTO();
            footballFieldDTO.setName(name);
            footballFieldDTO.setType(type);
            footballFieldDTO.setLocation(location);
            footballFieldDTO.setGeoPoint(geoPoint);
            footballFieldDTO.setGeoHash(geoHash);
            footballFieldDTO.setStatus(ACTIVE);

            list = new ArrayList<>();
            for (TimePickerDTO dto : timePickerAdapter.getTimePickerDTOList()) {
                if (dto.getPrice() > -1 && dto.getStart() > -1 && dto.getEnd() > -1) {
                    list.add(dto);
                }
            }
            if (uriImg != null) {
                uploadImageToStorage();
            } else {
                createFootballField();
            }
        }
    }

    private void uploadImageToStorage() {
        try {
            FootballFieldDAO dao = new FootballFieldDAO();
            dao.uploadImgFootballFieldToFirebase(uriImg)
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            try {
                                if (task.isSuccessful()) {
                                    Uri uri = task.getResult();
                                    footballFieldDTO.setImage(uri.toString());

                                    createFootballField();
                                    System.out.println(("URL IMAGE " + footballFieldDTO.getImage()));
                                } else {
                                    Toast.makeText(CreateFootballFieldActivity.this, "Update fail"
                                            , Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createFootballField() {
        try {
            FootballFieldDAO fieldDAO = new FootballFieldDAO();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            UserDAO userDAO = new UserDAO();
            userDAO.getUserById(user.getUid())
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            try {
                                UserDTO owner = documentSnapshot.get("userInfo",UserDTO.class);
                                fieldDAO.createNewFootballField(footballFieldDTO, owner, list)
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(CreateFootballFieldActivity.this, "Create Fail" + e.getMessage(), Toast.LENGTH_LONG).show();
                                                e.printStackTrace();
                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(CreateFootballFieldActivity.this, "Create Successfull", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateFootballFieldActivity.this,
                                    "Fail to get user on server", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isValidCreate(String name, String location, String type) {
        boolean result = true;
        utils.clearError(tlName);
        utils.clearError(tlLocation);
        utils.clearError(tlType);

        if (val.isEmpty(type)) {
            utils.showError(tlType, "Type must not be blank");
            result = false;
        }
        if(val.isEmpty(location)) {
            utils.showError(tlLocation, "Location must not be blank");
            result = false;
        }
        if(val.isEmpty(name)) {
            utils.showError(tlName, "Name must not be blank");
            result = false;
        }
        if(geoPoint == null) {
            Toast.makeText(this, "ERROR: Please choose geo point", Toast.LENGTH_SHORT).show();
            result = false;
        }
        return result;
    }
}