package com.example.football_field_booking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.UserDTO;
import com.example.football_field_booking.validations.Validation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileEditActivity extends AppCompatActivity {

    private TextView txtUserId, txtEmail;
    private TextInputLayout txtFullName, txtPhone;
    private Button btnUpdate;
    private UserDTO userDTO = null;
    private Validation val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        txtUserId = findViewById(R.id.txtUserID);
        txtEmail = findViewById(R.id.txtEmail);
        txtFullName = findViewById(R.id.txtFullName);
        txtPhone = findViewById(R.id.txtPhone);
        btnUpdate = findViewById(R.id.btnUpdateUser);
        val = new Validation();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (!val.isUser()) {
            Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            Intent intentBackToMain = new Intent(this, MainActivity.class);
            intentBackToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentBackToMain);
        }

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
}