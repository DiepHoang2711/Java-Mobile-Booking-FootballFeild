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
import android.widget.TextView;
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
import com.google.android.material.textfield.TextInputLayout;
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

    public static final int RC_GALLERY = 1;
    public static final String ACTIVE = "active";
    private Button btnChooseImg;
    private ImageView imgPhoto;
    private Uri uriImg;
    private StorageReference storageRef;
    private TextInputLayout txtName, txtLocation;
    private AutoCompleteTextView auComTxtType;
    private ImageButton imgBtnAdd;
    private TimePickerAdapter timePickerAdapter;
    private ListView lvTimePickerWorking;
    private List<String> listTypeField;
    private FootballFieldDTO footballFieldDTO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_football_field);

        txtName = findViewById(R.id.txtFootballFieldName);
        txtLocation = findViewById(R.id.txtLocation);
        auComTxtType = findViewById(R.id.auComTxtType);
        imgPhoto = findViewById(R.id.img_photo);
        btnChooseImg = findViewById(R.id.btnChooseImage);
        imgBtnAdd = findViewById(R.id.imgBtnAdd);
        lvTimePickerWorking = findViewById(R.id.lvTimePickerWorking);

        btnChooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, RC_GALLERY);
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
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateFootballFieldActivity.this, android.R.layout.simple_spinner_item, listTypeField);
                            auComTxtType.setAdapter(adapter);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        timePickerAdapter = new TimePickerAdapter(CreateFootballFieldActivity.this);
        loadTimePickerWorking();
        imgBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("timePicker",timePickerAdapter.getTimePickerDTOList().size()+"");
                loadTimePickerWorking();
            }
        });
    }

    private void loadTimePickerWorking(){
        TimePickerDTO timePickerDTO = new TimePickerDTO();
        timePickerDTO.setStart(-1);
        timePickerDTO.setEnd(-1);
        timePickerDTO.setPrice(-1);
        timePickerAdapter.getTimePickerDTOList().add(timePickerDTO);
        lvTimePickerWorking.setAdapter(timePickerAdapter);
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_GALLERY) {
            if(resultCode == RESULT_OK) {
                try {
                    uriImg = data.getData();
                    imgPhoto.setImageURI(uriImg);
                    Log.d("USER", "Success");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }


    public void clickToCreate(View view) {
        String name = txtName.getEditText().getText().toString();
        String location = txtLocation.getEditText().getText().toString();
        String type=auComTxtType.getText().toString();

        footballFieldDTO = new FootballFieldDTO();
        footballFieldDTO.setName(name);
        footballFieldDTO.setType(type);
        footballFieldDTO.setLocation(location);
        footballFieldDTO.setStatus(ACTIVE);

        List<TimePickerDTO> list=new ArrayList<>();
        for(TimePickerDTO dto:timePickerAdapter.getTimePickerDTOList()){
            if(dto.getPrice()>-1 && dto.getStart() >-1 && dto.getEnd() > -1){
                list.add(dto);
            }
        }
        FootballFieldDAO fieldDAO = new FootballFieldDAO();
        try {
            if(uriImg!=null){
                uploadImageToStorage();
            }
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
                                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    FootballFieldDTO fieldDTO=task.getResult().toObject(FootballFieldDTO.class);
                                                    try {
                                                        fieldDAO.addFootFiledInfoToUsersCollection(userDTO.getUserID(),fieldDTO);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                            Toast.makeText(CreateFootballFieldActivity.this, "Create Successfull", Toast.LENGTH_SHORT).show();
//                                            finish();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Toast.makeText(CreateFootballFieldActivity.this, "Create subCollection Failded", Toast.LENGTH_SHORT).show();

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

    private void uploadImageToStorage () {
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
                                    System.out.println(("URL IMAGE "+ footballFieldDTO.getImage()));
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
}