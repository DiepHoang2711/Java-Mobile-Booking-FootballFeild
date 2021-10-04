package com.example.football_field_booking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.UserDTO;
import com.example.football_field_booking.utils.Util;
import com.example.football_field_booking.validations.Validation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    public static final String EMAIL_LOG = "Email";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private Util util;

    private Button btnSignUp, btnLogin;
    private TextInputLayout txtEmail, txtFullName, txtPassword, txtConfirm;
    private ProgressDialog prdRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        util = new Util();

        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        txtEmail = findViewById(R.id.txtEmail);
        txtFullName = findViewById(R.id.txtFullName);
        txtPassword = findViewById(R.id.txtPassword);
        txtConfirm = findViewById(R.id.txtConfirm);
        prdRegister = new ProgressDialog(RegisterActivity.this);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtEmail.getEditText().getText().toString();
                String password = txtPassword.getEditText().getText().toString();
                String username = txtFullName.getEditText().getText().toString();
                String confirm = txtConfirm.getEditText().getText().toString();

                if(isValidRegister(email, password, username, confirm)) {
                    util.showProgressDialog(prdRegister, "Register", "Wait for register");
                    SignUpWithEmail(email, password);
                }else {
                    updateUI(null);
                }

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Validation validation = new Validation();
        if(validation.isUser()){
            updateUI(currentUser);
        }else {
            updateUI(null);
        }

    }

    private void SignUpWithEmail(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            try {
                                Log.d(EMAIL_LOG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                UserDAO dao = new UserDAO();
                                String username = txtFullName.getEditText().getText().toString();
                                UserDTO userDTO = new UserDTO(user.getUid(), user.getEmail(), username,
                                        "user", "active");

                                dao.createUser(userDTO);
                                sendEmailVerification();

                                updateUI(user);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(EMAIL_LOG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            util.showError(txtEmail, "Email is invalid");
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        prdRegister.cancel();
        if (user != null) {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void sendEmailVerification() {

        FirebaseUser user = mAuth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(EMAIL_LOG, "Email sent.");
                            Toast.makeText(RegisterActivity.this, "Email send to verify!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean isValidRegister (String email, String password, String username, String confirm) {
        util.clearError(txtEmail);
        util.clearError(txtPassword);
        util.clearError(txtConfirm);
        util.clearError(txtFullName);

        boolean result = true;

        if(password.trim().isEmpty() || password.length() < 8) {
            util.showError(txtPassword, "Password must be 8 character");
            result = false;
        }
        if(confirm.trim().isEmpty() || !confirm.equals(password)) {
            util.showError(txtConfirm, "Confirm must be a match");
            result = false;
        }
        if(username.trim().isEmpty()){
            util.showError(txtFullName, "Username must be blank");
            result = false;
        }
        if(email.trim().isEmpty() || !email.contains("@")){
            util.showError(txtEmail, "Email is invalid");
            result = false;
        }
        return result;
    }

}