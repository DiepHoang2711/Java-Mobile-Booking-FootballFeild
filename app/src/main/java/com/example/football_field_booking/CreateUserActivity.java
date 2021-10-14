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
import android.widget.Toast;

import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.UserDTO;
import com.example.football_field_booking.utils.Utils;
import com.example.football_field_booking.validations.Validation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class CreateUserActivity extends AppCompatActivity {

    public static final int RC_IMAGE_PICKER = 1001;

    private TextInputLayout txtFullName, txtPhone, txtEmail, txtPassword, txtConfirm, tilRole, tilStatus;
    private AutoCompleteTextView txtRole, txtStatus;
    private Button btnCreate, btnChoiceImage;
    private ImageView imgUser;
    private List<String> roles, status;
    private Utils util;
    private Uri imgUri = null;
    private FirebaseAuth mAuth, authCreate;
    private ProgressDialog progressDialog;
    private UserDTO userDTO = null;
    private Validation val;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        txtEmail = findViewById(R.id.txtEmail);
        txtFullName = findViewById(R.id.txtFullName);
        txtPhone = findViewById(R.id.txtPhone);
        txtRole = findViewById(R.id.txtRole);
        txtStatus = findViewById(R.id.txtStatus);
        txtPassword = findViewById(R.id.txtPassword);
        txtConfirm = findViewById(R.id.txtConfirm);
        btnCreate = findViewById(R.id.btnCreateUser);
        btnChoiceImage = findViewById(R.id.btnChoiceImage);
        tilRole = findViewById(R.id.tilRole);
        tilStatus = findViewById(R.id.tilStatus);
        imgUser = findViewById(R.id.imgUser);
        progressDialog = new ProgressDialog(this);
        util = new Utils();
        val = new Validation();
        mAuth = FirebaseAuth.getInstance();
        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setDatabaseUrl("https://booking-football-field.firebaseio.com")
                .setApiKey("AIzaSyDt5y74ZKMA6xgVavg1Hhmo3vdML5Zufik")
                .setApplicationId("booking-football-field").build();
        try {
            FirebaseApp myApp = FirebaseApp.initializeApp(getApplicationContext(), firebaseOptions, "AnyAppName");
            authCreate = FirebaseAuth.getInstance(myApp);
        } catch (IllegalStateException e){
            authCreate = FirebaseAuth.getInstance(FirebaseApp.getInstance("AnyAppName"));
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
                                ArrayAdapter<String> adapterRoles = new ArrayAdapter<>(CreateUserActivity.this, android.R.layout.simple_spinner_item,roles);
                                ArrayAdapter<String> adapterStatus = new ArrayAdapter<>(CreateUserActivity.this, android.R.layout.simple_spinner_item,status);
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

        btnChoiceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, RC_IMAGE_PICKER);
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtEmail.getEditText().getText().toString();
                String fullName = txtFullName.getEditText().getText().toString();
                String phone = txtPhone.getEditText().getText().toString();
                String role = txtRole.getText().toString();
                String status = txtStatus.getText().toString();
                password = txtPassword.getEditText().getText().toString();
                String confirm = txtConfirm.getEditText().getText().toString();
                if ( isValidCreate(email, password, confirm, fullName, phone, role, status)) {
                    userDTO = new UserDTO(null, email, fullName, phone, role, status,null);
                    util.showProgressDialog(progressDialog, "Create user", "Please wait for create");
                    if(imgUri != null) {
                        uploadImageToStorage();
                    }else {
                        createUser(userDTO, password);
                    }

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
                    imgUri = data.getData();
                    imgUser.setImageURI(imgUri);
                    Log.d("USER", "Success");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private boolean isValidCreate (String email,String password,String confirm,
                                   String fullName,String phone,String role,String status) {
        util.clearError(txtEmail);
        util.clearError(txtPassword);
        util.clearError(txtConfirm);
        util.clearError(txtFullName);
        util.clearError(txtPhone);
        util.clearError(tilRole);
        util.clearError(tilStatus);

        Log.d("USER", "role: " + role);
        Log.d("USER", "status: " + status);

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
        if(!val.isValidPassword(password)) {
            util.showError(txtPassword, "Password must be 8 character");
            result = false;
        }
        if(val.isEmpty(confirm) || !confirm.equals(password)) {
            util.showError(txtConfirm, "Confirm must be a match");
            result = false;
        }
        if(!val.isValidEmail(email)){
            util.showError(txtEmail, "Email is invalid");
            result = false;
        }
        return result;
    }

    private void createUser(UserDTO userDTO, String password) {

        authCreate.createUserWithEmailAndPassword(userDTO.getEmail(), password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            try {
                                Log.d("EMAIL", "createUserWithEmail:success");
                                FirebaseUser user = authCreate.getCurrentUser();
                                UserDAO dao = new UserDAO();
                                userDTO.setUserID(user.getUid());

                                dao.createUser(userDTO).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            Toast.makeText(CreateUserActivity.this,
                                                    "Create Success", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                                sendEmailVerification();
                                authCreate.signOut();
                                progressDialog.cancel();
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("EMAIL", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CreateUserActivity.this, "Create failed.",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                        }
                    }
                });
    }

    public void sendEmailVerification() {

        FirebaseUser user = mAuth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("EMAIL", "Email sent.");
                            Toast.makeText(CreateUserActivity.this, "Email send to verify!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void uploadImageToStorage () {
        try {

            UserDAO userDAO = new UserDAO();
            userDAO.uploadImgUserToFirebase(imgUri)
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            try {
                                if (task.isSuccessful()) {
                                    Log.d("USER", task.getResult().toString());
                                    Uri uri = task.getResult();
                                    userDTO.setPhotoUri(uri.toString());
                                    createUser(userDTO, password);
                                } else {
                                    Toast.makeText(CreateUserActivity.this, "Update fail"
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