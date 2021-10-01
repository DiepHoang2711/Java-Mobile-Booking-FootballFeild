package com.example.football_field_booking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.football_field_booking.daos.FootballFieldDAO;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CreateFootballFieldActivity extends AppCompatActivity {

    private Button btnChooseImg;
    private ImageView imgPhoto;
    private Uri uriImg;
    private StorageReference storageRef;
    private EditText edtName, edtLocation;
    private Spinner spType;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_football_field);

        edtName = findViewById(R.id.edtFootballFieldName);
        edtLocation = findViewById(R.id.edtLocation);
        spType = findViewById(R.id.spType);
        imgPhoto = findViewById(R.id.img_photo);
        btnChooseImg = findViewById(R.id.btnChooseImage);

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
        List<String> dataSrc = new ArrayList<>();
        dataSrc.add("5 people");
        dataSrc.add("7 people");
        dataSrc.add("11 people");
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dataSrc);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(adapter);
        type = dataSrc.get(0);

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
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = spType.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        String typeField = type;
        FootballFieldDTO footballFieldDTO = new FootballFieldDTO(name, location, typeField);
        FootballFieldDAO fieldDAO = new FootballFieldDAO();
        fieldDAO.createNewFootballField(footballFieldDTO)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateFootballFieldActivity.this, "Create Faild"+e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //thay bằng dialog
                        Toast.makeText(CreateFootballFieldActivity.this, "Create Successfull", Toast.LENGTH_SHORT).show();
                        Log.d("CreateFieldActivity", "DocumentSnapshot written with ID: " + documentReference.getId());
                        uploadImageToFirebase(documentReference.getId());
                        Intent intent = new Intent(CreateFootballFieldActivity.this,OwnerHomeActivity.class);
                        startActivity(intent);
                }
            });
    }


    private String getFileExtension(Uri uri) {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));
    }

    private void uploadImageToFirebase(String idPhoto) {
        storageRef = FirebaseStorage.getInstance().getReference("football_field_images");
        StorageReference imageRef = storageRef.child(idPhoto + "." + getFileExtension(uriImg));
        imgPhoto.setDrawingCacheEnabled(true);
        imgPhoto.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imgPhoto.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(CreateFootballFieldActivity.this, "Upload Image Faild!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...

            }
        });
    }

}