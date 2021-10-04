package com.example.football_field_booking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.UserDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfileEditByAdminActivity extends AppCompatActivity {

    private TextView txtUserId, txtEmail;
    private TextInputLayout txtFullName, txtPhone;
    private AutoCompleteTextView txtRole, txtStatus;
    private Button btnUpdate, btnDelete;
    private UserDTO userDTO = null;
    private List<String> roles, status;

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
                                txtRole.setText(userDTO.getRole());
                                txtStatus.setText(userDTO.getStatus());

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
                                } else {
                                    Toast.makeText(ProfileEditByAdminActivity.this,
                                            R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }
}