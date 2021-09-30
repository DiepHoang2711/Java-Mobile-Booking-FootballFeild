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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAO {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private static final String COLLECTION_USERS = "users";
    private static final String GOOGLE_LOG = "Google Log:";
    private UserDTO user;
    private List<UserDTO> listUser;

    public UserDAO() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public void createUser(UserDTO userDTO) {

        DocumentReference doc = db.collection(COLLECTION_USERS).document(userDTO.getUserID());
        Map<String, Object> data = new HashMap<>();
        data.put("userInfo", userDTO);

        doc.set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(GOOGLE_LOG, "DocumentSnapshot added with ID: " + doc.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(GOOGLE_LOG, "Error adding document", e);
                    }
                });
    }

    public UserDTO getUserById(String id){
        user = null;
        DocumentReference doc = db.collection(COLLECTION_USERS).document(id);
        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(UserDTO.class);
            }
        });
        return user;
    }

    public List<UserDTO> getAllUser() {
        listUser = null;
        db.collection(COLLECTION_USERS)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        listUser = new ArrayList<>();
                        for (DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()) {
                            listUser.add(doc.toObject(UserDTO.class));
                        }
                    }
                });
        return listUser;
    }

    public void updateUser(UserDTO userDTO) {
        DocumentReference doc = db.collection(COLLECTION_USERS).document(userDTO.getUserID());
        Map<String, Object> data = new HashMap<>();
        data.put("userInfo", userDTO);
        FirebaseUser user = mAuth.getCurrentUser();

        doc.update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(GOOGLE_LOG, "DocumentSnapshot update with ID: " + doc.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(GOOGLE_LOG, "Error update document", e);
                    }
                });


        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(userDTO.getUsername())
                .setPhotoUri(Uri.parse(userDTO.getPhotoUri()))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(GOOGLE_LOG, "User profile updated.");
                        }
                    }
                });
        user.updateEmail(userDTO.getEmail())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(GOOGLE_LOG, "User email address updated.");
                        }
                    }
                });
    }
}
