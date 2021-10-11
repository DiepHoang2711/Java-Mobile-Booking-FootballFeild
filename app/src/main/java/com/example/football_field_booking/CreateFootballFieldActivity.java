package com.example.football_field_booking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class CreateFootballFieldActivity extends AppCompatActivity {

    private Button btnChooseImg;
    private ImageView imgPhoto;
    private Uri uriImg;
    private StorageReference storageRef;
    private EditText edtName, edtLocation;
    private AutoCompleteTextView auComTxtType;
    private String type;
    private EditText edtStartTime, edtEndTime, edtPrice;
    private ImageButton imgBtnAdd;
    private List<TimePickerDTO> timePickerDTOList;
    private TimePickerAdapter timePickerAdapter;
    private ListView lvTimePickerWorking;
    private List<String> listTypeField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_football_field);

        edtName = findViewById(R.id.edtFootballFieldName);
        edtLocation = findViewById(R.id.edtLocation);
        auComTxtType = findViewById(R.id.auComTxtType);
        imgPhoto = findViewById(R.id.img_photo);
        btnChooseImg = findViewById(R.id.btnChooseImage);
        edtStartTime = findViewById(R.id.edtStartTime);
        edtEndTime = findViewById(R.id.edtEndTime);
        edtPrice = findViewById(R.id.edtPrice);
        imgBtnAdd = findViewById(R.id.imgBtnAdd);
        lvTimePickerWorking = findViewById(R.id.lvTimePickerWorking);

        btnChooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        //load list type tu database
        FootballFieldDAO footballFieldDAO = new FootballFieldDAO();
        try {
            footballFieldDAO.getTypeOfFootballField()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            listTypeField = (ArrayList<String>) task.getResult().get("TypeFootBallFIeld");
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateFootballFieldActivity.this, R.layout.item_type_football_field, listTypeField);
                            auComTxtType.setAdapter(adapter);
                            auComTxtType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    type = adapterView.getItemAtPosition(i).toString();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    type = listTypeField.get(0);
                                }
                            });
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

        timePickerDTOList = new ArrayList<>();
        TimePickerDTO timePickerDTO = new TimePickerDTO();
        loadTimePickerWorking(timePickerDTO);
        imgBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDTO timePickerDTO = new TimePickerDTO();
                int startTime = Integer.parseInt(edtStartTime.getText().toString().split(":")[0]);
                int endTime = Integer.parseInt(edtStartTime.getText().toString().split(":")[0]);
                float price = Float.parseFloat(edtStartTime.getText().toString());
                timePickerDTO.setStart(startTime);
                timePickerDTO.setEnd(endTime);
                timePickerDTO.setPrice(price);
                loadTimePickerWorking(timePickerDTO);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void loadTimePickerWorking(TimePickerDTO timePickerDTO) {
        timePickerDTOList.add(timePickerDTO);
        timePickerAdapter = new TimePickerAdapter(CreateFootballFieldActivity.this, timePickerDTOList);
        lvTimePickerWorking.setAdapter(timePickerAdapter);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriImg = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriImg);
                imgPhoto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public void clickToCreate(View view) {
        String name = edtName.getText().toString();
        String location = edtLocation.getText().toString();

        FootballFieldDTO footballFieldDTO = new FootballFieldDTO();
        footballFieldDTO.setName(name);
        footballFieldDTO.setType(type);
        footballFieldDTO.setLocation(location);
        FootballFieldDAO fieldDAO = new FootballFieldDAO();
        try {
            fieldDAO.createNewFootballField(footballFieldDTO, uriImg)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateFootballFieldActivity.this, "Create Faild" + e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    //thay bằng dialog
                    try {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        UserDAO userDAO = new UserDAO();
                        userDAO.getUserById(user.getUid())
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        try {
                                            UserDTO userDTO = documentSnapshot.toObject(UserDTO.class);
                                            fieldDAO.addOwnerToFootballFieldsCollection(userDTO, documentReference);
                                            fieldDAO.addFootFiledInfoToUsersCollection(userDTO.getUserID(), Objects.requireNonNull(documentReference.get().getResult().toObject(FootballFieldDTO.class)));
                                            Toast.makeText(CreateFootballFieldActivity.this, "Create Successfull", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } catch (Exception e) {
                                            Log.d("DAO", e.toString());
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
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}