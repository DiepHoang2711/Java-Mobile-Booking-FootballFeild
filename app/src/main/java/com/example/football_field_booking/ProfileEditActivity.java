package com.example.football_field_booking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.UserDTO;
import com.example.football_field_booking.utils.Util;
import com.example.football_field_booking.validations.Validation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;

public class ProfileEditActivity extends AppCompatActivity {

    public static final int RC_IMAGE_PICKER = 1000;
    private TextView txtUserId, txtEmail;
    private TextInputLayout txtFullName, txtPhone;
    private Button btnUpdate;
    private ImageView imgUser;
    private UserDTO userDTO = null;
    private Validation val;
    private Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        txtUserId = findViewById(R.id.txtUserID);
        txtEmail = findViewById(R.id.txtEmail);
        txtFullName = findViewById(R.id.txtFullName);
        txtPhone = findViewById(R.id.txtPhone);
        btnUpdate = findViewById(R.id.btnUpdateUser);
        imgUser = findViewById(R.id.imgUser);
        val = new Validation();
        util = new Util();

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
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, RC_IMAGE_PICKER);
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
                        Toast.makeText(ProfileEditActivity.this,
                                "Fail to get user on server", Toast.LENGTH_SHORT).show();
                    }
                });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userDTO != null) {
                    String fullName = txtFullName.getEditText().getText().toString();
                    String phone = txtPhone.getEditText().getText().toString();

                    userDTO.setFullName(fullName);
                    userDTO.setPhone(phone);

                    userDAO.updateUser(userDTO)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        finish();
                                        startActivity(ProfileEditActivity.this.getIntent());
                                    } else {
                                        Toast.makeText(ProfileEditActivity.this,
                                                "Fail to update User", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else {
                    Toast.makeText(ProfileEditActivity.this,
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
                  ProgressDialog progressDialog = new ProgressDialog(ProfileEditActivity.this);
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
                                                      Toast.makeText(ProfileEditActivity.this, "Update Success"
                                                              , Toast.LENGTH_SHORT).show();
                                                      finish();
                                                      startActivity(ProfileEditActivity.this.getIntent());
                                                      overridePendingTransition(0, 0);
                                                  }else {
                                                      Toast.makeText(ProfileEditActivity.this, "Update fail"
                                                              , Toast.LENGTH_SHORT).show();
                                                  }
                                              }
                                          });
                                      } else {
                                          Toast.makeText(ProfileEditActivity.this, "Update fail"
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
}