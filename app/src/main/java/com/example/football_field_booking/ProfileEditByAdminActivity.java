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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.UserDTO;
import com.example.football_field_booking.utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfileEditByAdminActivity extends AppCompatActivity {

    public static final int RC_IMAGE_PICKER = 1000;

    private TextView txtUserId, txtEmail;
    private TextInputLayout txtFullName, txtPhone;
    private AutoCompleteTextView txtRole, txtStatus;
    private Button btnUpdate, btnDelete;
    private ImageView imgUser;
    private UserDTO userDTO = null;
    private List<String> roles, status;
    private Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit_by_admin);

        txtUserId = findViewById(R.id.txtUserID);
        txtEmail = findViewById(R.id.txtEmail);
        txtFullName = findViewById(R.id.txtFullName);
        txtPhone = findViewById(R.id.txtPhone);
        txtRole = findViewById(R.id.txtRole);
        txtStatus = findViewById(R.id.txtStatus);
        btnUpdate = findViewById(R.id.btnUpdateUser);
        btnDelete = findViewById(R.id.btnDeleteUser);
        imgUser = findViewById(R.id.imgUser);
        util = new Util();

        Intent intent = this.getIntent();
        String userID = intent.getStringExtra("userID");
        if(userID == null) {
            Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            Intent intentBackToMain = new Intent(this, MainActivity.class);
            intentBackToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentBackToMain);
        }

        UserDAO userDAO = new UserDAO();

        userDAO.getConstOfUser()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            try {
                                roles = (List<String>) task.getResult().get("roles");
                                status = (List<String>) task.getResult().get("status");
                                ArrayAdapter<String> adapterRoles = new ArrayAdapter<>(ProfileEditByAdminActivity.this, android.R.layout.simple_spinner_item,roles);
                                ArrayAdapter<String> adapterStatus = new ArrayAdapter<>(ProfileEditByAdminActivity.this, android.R.layout.simple_spinner_item,status);
                                txtRole.setAdapter(adapterRoles);
                                txtStatus.setAdapter(adapterStatus);
                                Log.d("USER", roles.toString());
                                Log.d("USER", status.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        userDAO.getUserById(userID)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            try {
                                userDTO = task.getResult().toObject(UserDTO.class);

                                txtUserId.setText(userDTO.getUserID());
                                txtEmail.setText(userDTO.getEmail());
                                txtPhone.getEditText().setText(userDTO.getPhone());
                                txtFullName.getEditText().setText(userDTO.getFullName());
                                txtRole.setText(userDTO.getRole(), false);
                                txtStatus.setText(userDTO.getStatus(), false);
                                Log.d("USER", userDTO.getPhotoUri());
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
                        } else {
                            Toast.makeText(ProfileEditByAdminActivity.this,
                                    "Fail to get user on server", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userDTO != null) {
                    String fullName = txtFullName.getEditText().getText().toString();
                    String phone = txtPhone.getEditText().getText().toString();
                    String role = txtRole.getText().toString();
                    String status = txtStatus.getText().toString();

                    userDTO.setFullName(fullName);
                    userDTO.setPhone(phone);
                    userDTO.setRole(role);
                    userDTO.setStatus(status);

                    userDAO.updateUser(userDTO)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        finish();
                                        startActivity(ProfileEditByAdminActivity.this.getIntent());
                                    } else {
                                        Toast.makeText(ProfileEditByAdminActivity.this,
                                                "Fail to update User", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else {
                    Toast.makeText(ProfileEditByAdminActivity.this,
                            R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userDAO.deleteUser(userID)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(ProfileEditByAdminActivity.this,
                                            "Delete successful", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(ProfileEditByAdminActivity.this.getIntent());
                                } else {
                                    Toast.makeText(ProfileEditByAdminActivity.this,
                                            R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, RC_IMAGE_PICKER);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_IMAGE_PICKER) {
            if (resultCode == RESULT_OK) {

                try {
                    Uri uri = data.getData();
                    UserDAO userDAO = new UserDAO();
                    ProgressDialog progressDialog = new ProgressDialog(ProfileEditByAdminActivity.this);
                    util.showProgressDialog(progressDialog, "Uploading ....", "Please wait for uploading image");
                    userDAO.uploadImgUserToFirebase(uri)
                            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    try {
                                        if (task.isSuccessful()) {
                                            Log.d("USER", task.getResult().toString());
                                            userDTO.setPhotoUri(task.getResult().toString());
                                            userDAO.updateUser(userDTO).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressDialog.cancel();
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(ProfileEditByAdminActivity.this, "Update Success"
                                                                , Toast.LENGTH_SHORT).show();
                                                        finish();
                                                        startActivity(ProfileEditByAdminActivity.this.getIntent());
                                                    } else {
                                                        Toast.makeText(ProfileEditByAdminActivity.this, "Update fail"
                                                                , Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(ProfileEditByAdminActivity.this, "Update fail"
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
    }
}