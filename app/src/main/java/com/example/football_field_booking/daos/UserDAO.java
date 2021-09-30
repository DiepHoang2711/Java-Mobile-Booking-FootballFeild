package com.example.football_field_booking.daos;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.football_field_booking.dtos.UserDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAO {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private static final String COLLECTION_USERS = "users";

    public UserDAO() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public Task<Void> createUser(UserDTO userDTO) {
        DocumentReference doc = db.collection(COLLECTION_USERS).document(userDTO.getUserID());
        return doc.set(userDTO);
    }


    public Task<DocumentSnapshot> getUserById(String id){
        DocumentReference doc = db.collection(COLLECTION_USERS).document(id);
        return doc.get();
    }

    public Task<QuerySnapshot> getAllUser() {
        return db.collection(COLLECTION_USERS).get();
    }

    public Task<Void> updateUser(UserDTO userDTO) {

        DocumentReference doc = db.collection(COLLECTION_USERS).document(userDTO.getUserID());
        Map<String, Object> data = new HashMap<>();
        data.put("userID", userDTO.getUserID());
        data.put("email", userDTO.getEmail());
        data.put("fullName", userDTO.getFullName());
        data.put("phone", userDTO.getPhone());
        data.put("role", userDTO.getUserID());
        data.put("status", userDTO.getUserID());
        data.put("photoUrl", userDTO.getUserID());

        return doc.update(data);
    }

    public Task<Void> updateEmail (String email) {
        FirebaseUser user = mAuth.getCurrentUser();
        return user.updateEmail(email);
    }

    public Task<Void> updatePassword (String password) {
        FirebaseUser user = mAuth.getCurrentUser();
        return user.updatePassword(password);
    }

}
