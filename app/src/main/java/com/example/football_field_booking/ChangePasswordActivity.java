package com.example.football_field_booking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.utils.Utils;
import com.example.football_field_booking.validations.Validation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private MaterialToolbar topAppBar;
    private TextInputLayout tlOldPassword, tlNewPassword, tlConfirm;
    private Button btnChangePassword;
    private ProgressDialog prdChangePassword;
    private Utils utils;
    private Validation val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        topAppBar=findViewById(R.id.topAppBar);
        tlOldPassword = findViewById(R.id.tlOldPassword);
        tlNewPassword = findViewById(R.id.tlNewPassword);
        tlConfirm = findViewById(R.id.tlConfirm);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        prdChangePassword = new ProgressDialog(ChangePasswordActivity.this);
        utils = new Utils();
        val = new Validation();

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPassword = tlOldPassword.getEditText().getText().toString();
                String newPassword = tlNewPassword.getEditText().getText().toString();
                String confirm = tlConfirm.getEditText().getText().toString();

                if(isValidChangePassword(oldPassword, newPassword, confirm)) {
                    try {
                        utils.showProgressDialog(prdChangePassword, "Change password", "Please wait for changing password");
                        changePassword(oldPassword, newPassword);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    private void changePassword(String oldPassword, String newPassword) throws Exception{
        // reAuthentication
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {

            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        UserDAO userDAO = new UserDAO();
                        userDAO.updatePassword(newPassword, user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(ChangePasswordActivity.this, "Change password successfully", Toast.LENGTH_SHORT).show();
                                    prdChangePassword.cancel();
                                    finish();
                                }else {
                                    Toast.makeText(ChangePasswordActivity.this, "Fail to change password", Toast.LENGTH_SHORT).show();
                                    prdChangePassword.cancel();
                                }
                            }
                        });
                    }else {
                        utils.showError(tlOldPassword, "Wrong old password");
                        prdChangePassword.cancel();
                    }
                }
            });

        }else {
            Toast.makeText(this, getResources().getString(R.string.something_went_wrong)
                    , Toast.LENGTH_SHORT).show();
            prdChangePassword.cancel();
        }

    }

    private boolean isValidChangePassword( String oldPassword, String newPassword, String confirm) {
        utils.clearError(tlOldPassword);
        utils.clearError(tlNewPassword);
        utils.clearError(tlConfirm);

        boolean result = true;
        if(!val.isValidPassword(oldPassword)) {
            utils.showError(tlOldPassword, "Old password must be more than 8 character");
            result = false;
        }

        if(!val.isValidPassword(newPassword)) {
            utils.showError(tlNewPassword, "New Password must be more than 8 character");
            result = false;
        }
        if(val.isEmpty(confirm) || !confirm.equals(newPassword)) {
            utils.showError(tlConfirm, "Confirm must be a match");
            result = false;
        }
        return result;
    }
}