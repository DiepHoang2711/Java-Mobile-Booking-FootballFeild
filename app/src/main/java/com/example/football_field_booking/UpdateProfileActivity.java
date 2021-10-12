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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.UserDTO;
import com.example.football_field_booking.utils.Utils;
import com.example.football_field_booking.validations.Validation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

public class UpdateProfileActivity extends AppCompatActivity {

    public static final int RC_IMAGE_PICKER = 1000;
    private TextView txtUserId, txtEmail;
    private TextInputLayout txtFullName, txtPhone;
    private Button btnUpdate;
    private ImageView imgUser;
    private UserDTO userDTO = null;
    private Validation val;
    private Utils util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        txtUserId = findViewById(R.id.txtUserID);
        txtEmail = findViewById(R.id.txtEmail);
        txtFullName = findViewById(R.id.txtFullName);
        txtPhone = findViewById(R.id.txtPhone);
        btnUpdate = findViewById(R.id.btnUpdateUser);
        imgUser = findViewById(R.id.imgUser);
        val = new Validation();
        util = new Utils();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (!val.isUser()) {
            Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            Intent intentBackToMain = new Intent(this, MainActivity.class);
            intentBackToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentBackToMain);
        }

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, RC_IMAGE_PICKER);
            }
        });

        UserDAO userDAO = new UserDAO();

        userDAO.getUserById(user.getUid())
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        try {
                            userDTO = documentSnapshot.toObject(UserDTO.class);

                            Log.d("USER","Is verify: " + String.valueOf(user.isEmailVerified()));
                            txtUserId.setText(userDTO.getUserID());
                            txtEmail.setText(userDTO.getEmail());
                            txtPhone.getEditText().setText(userDTO.getPhone());
                            txtFullName.getEditText().setText(userDTO.getFullName());
                            if (userDTO.getPhotoUri() != null) {
                                Uri uri = Uri.parse(userDTO.getPhotoUri());
                                Glide.with(imgUser.getContext())
                                        .load(uri)
                                        .into(imgUser);
                            } else {
                                imgUser.setImageResource(R.drawable.outline_account_circle_24);
                            }

                        } catch (Exception e) {
                            Log.d("DAO", e.toString());
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateProfileActivity.this,
                                "Fail to get user on server", Toast.LENGTH_SHORT).show();
                    }
                });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userDTO != null) {
                    String fullName = txtFullName.getEditText().getText().toString();
                    String phone = txtPhone.getEditText().getText().toString();

                    if(isValidUpdate(fullName, phone)) {
                        userDTO.setFullName(fullName);
                        userDTO.setPhone(phone);
                        userDAO.updateUser(userDTO)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            finish();
                                            startActivity(UpdateProfileActivity.this.getIntent());
                                        } else {
                                            Toast.makeText(UpdateProfileActivity.this,
                                                    "Fail to update User", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }

                }else {
                    Toast.makeText(UpdateProfileActivity.this,
                            R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_IMAGE_PICKER) {
            if(resultCode == RESULT_OK) {

              try {
                  Uri uri = data.getData();
                  UserDAO userDAO = new UserDAO();
                  ProgressDialog progressDialog = new ProgressDialog(UpdateProfileActivity.this);
                  util.showProgressDialog(progressDialog, "Uploading ....", "Please wait for uploading image");
                  userDAO.uploadImgUserToFirebase(uri)
                          .addOnCompleteListener(new OnCompleteListener<Uri>() {
                              @Override
                              public void onComplete(@NonNull Task<Uri> task) {
                                  try {
                                      if(task.isSuccessful()) {
                                          userDTO.setPhotoUri(task.getResult().toString());
                                          userDAO.updateUser(userDTO).addOnCompleteListener(new OnCompleteListener<Void>() {
                                              @Override
                                              public void onComplete(@NonNull Task<Void> task) {
                                                  progressDialog.cancel();
                                                  if (task.isSuccessful()) {
                                                      Toast.makeText(UpdateProfileActivity.this, "Update Success"
                                                              , Toast.LENGTH_SHORT).show();
                                                      finish();
                                                      startActivity(UpdateProfileActivity.this.getIntent());
                                                      overridePendingTransition(0, 0);
                                                  }else {
                                                      Toast.makeText(UpdateProfileActivity.this, "Update fail"
                                                              , Toast.LENGTH_SHORT).show();
                                                  }
                                              }
                                          });
                                      } else {
                                          Toast.makeText(UpdateProfileActivity.this, "Update fail"
                                                  , Toast.LENGTH_SHORT).show();
                                      }
                                  }catch (Exception e) {
                                      e.printStackTrace();
                                  }
                              }
                          });
              }catch (Exception e) {
                  e.printStackTrace();
              }

            }
        }
    }

    private boolean isValidUpdate ( String fullName,String phone) {
        util.clearError(txtFullName);
        util.clearError(txtPhone);

        boolean result = true;
        if(!val.isEmpty(phone)) {
            if(!val.isValidPhoneNumber(phone) ) {
                util.showError(txtPhone, "Phone must be between 8 and 11 number");
                result = false;
            }
        }
        if(val.isEmpty(fullName)){
            util.showError(txtFullName, "Username must not be blank");
            result = false;
        }
        return result;
    }
}