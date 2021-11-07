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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.example.football_field_booking.dtos.UserDTO;
import com.example.football_field_booking.dtos.UserDocument;
import com.example.football_field_booking.utils.Utils;
import com.example.football_field_booking.validations.Validation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class EditProfileByAdminActivity extends AppCompatActivity {

    public static final int RC_IMAGE_PICKER = 1000;

    private TextInputLayout txtFullName, txtPhone, tilRole, tilStatus,txtEmail;
    private AutoCompleteTextView txtRole, txtStatus;
    private Button btnUpdate, btnDelete;
    private ImageView imgUser;
    private UserDTO userDTO = null;
    private List<String> roles, status;
    private List<FootballFieldDTO> fieldDTOList;
    private Utils util;
    private Validation val;

    private MaterialToolbar topAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_by_admin);

        txtEmail = findViewById(R.id.txtEmail);
        txtFullName = findViewById(R.id.txtFullName);
        txtPhone = findViewById(R.id.txtPhone);
        txtRole = findViewById(R.id.txtRole);
        txtStatus = findViewById(R.id.txtStatus);
        btnUpdate = findViewById(R.id.btnUpdateUser);
        tilRole = findViewById(R.id.tilRole);
        tilStatus = findViewById(R.id.tilStatus);
        btnDelete = findViewById(R.id.btnDeleteUser);
        imgUser = findViewById(R.id.imgUser);
        util = new Utils();
        val = new Validation();
        topAppBar=findViewById(R.id.topAppBar);

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Intent intent = this.getIntent();
        String userID = intent.getStringExtra("userID");
        if(userID == null) {
            Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            finish();
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
                                ArrayAdapter<String> adapterRoles = new ArrayAdapter<>(EditProfileByAdminActivity.this, android.R.layout.simple_spinner_item,roles);
                                ArrayAdapter<String> adapterStatus = new ArrayAdapter<>(EditProfileByAdminActivity.this, android.R.layout.simple_spinner_item,status);
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
                                userDTO = task.getResult().get("userInfo",UserDTO.class);

                                txtEmail.getEditText().setText(userDTO.getEmail());
                                txtEmail.setEnabled(false);
                                txtPhone.getEditText().setText(userDTO.getPhone());
                                txtFullName.getEditText().setText(userDTO.getFullName());
                                txtRole.setText(userDTO.getRole(), false);
                                txtStatus.setText(userDTO.getStatus(), false);
                                Log.d("USER", userDTO.getPhotoUri());

                                try {
                                    Uri uri = Uri.parse(userDTO.getPhotoUri());
                                    Glide.with(imgUser.getContext())
                                            .load(uri)
                                            .into(imgUser);
                                }catch (Exception e) {
                                    imgUser.setImageResource(R.drawable.outline_account_circle_24);
                                }

                                if(userDTO.getStatus().equals("inactive")){
                                    btnDelete.setVisibility(View.GONE);
                                }

                                if(userDTO.getRole().equals("owner")){
                                    fieldDTOList = task.getResult().toObject(UserDocument.class).getFieldsInfo();
                                }

                            } catch (Exception e) {
                                Log.d("DAO", e.toString());
                            }
                        } else {
                            Toast.makeText(EditProfileByAdminActivity.this,
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

                    if(isValidUpdate(fullName, phone, role, status)) {
                        userDTO.setFullName(fullName);
                        userDTO.setPhone(phone);
                        userDTO.setRole(role);
                        userDTO.setStatus(status);

                        userDAO.updateUser(userDTO, fieldDTOList)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            finish();
                                            startActivity(EditProfileByAdminActivity.this.getIntent());
                                        } else {
                                            Toast.makeText(EditProfileByAdminActivity.this,
                                                    "Fail to update User", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }

                }else {
                    Toast.makeText(EditProfileByAdminActivity.this,
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
                                    Toast.makeText(EditProfileByAdminActivity.this,
                                            "Delete successful", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(EditProfileByAdminActivity.this.getIntent());
                                } else {
                                    Toast.makeText(EditProfileByAdminActivity.this,
                                            R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, RC_IMAGE_PICKER);
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
                    ProgressDialog progressDialog = new ProgressDialog(EditProfileByAdminActivity.this);
                    util.showProgressDialog(progressDialog, "Uploading ....", "Please wait for uploading image");
                    userDAO.uploadImgUserToFirebase(uri)
                            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    try {
                                        if (task.isSuccessful()) {
                                            Log.d("USER", task.getResult().toString());
                                            userDTO.setPhotoUri(task.getResult().toString());
                                            userDAO.updateUser(userDTO, fieldDTOList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressDialog.cancel();
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(EditProfileByAdminActivity.this, "Update Success"
                                                                , Toast.LENGTH_SHORT).show();
                                                        finish();
                                                        startActivity(EditProfileByAdminActivity.this.getIntent());
                                                    } else {
                                                        Toast.makeText(EditProfileByAdminActivity.this, "Update fail"
                                                                , Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(EditProfileByAdminActivity.this, "Update fail"
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


    private boolean isValidUpdate ( String fullName,String phone,String role,String status) {
        util.clearError(txtFullName);
        util.clearError(txtPhone);
        util.clearError(tilRole);
        util.clearError(tilStatus);

        boolean result = true;
        if(val.isEmpty(status)) {
            util.showError(tilStatus, "Status must not be blank");
            result = false;
        }
        if(val.isEmpty(role)) {
            util.showError(tilRole, "Role must not be blank");
            result = false;
        }
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