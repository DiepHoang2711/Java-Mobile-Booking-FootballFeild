package com.example.football_field_booking.daos;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.football_field_booking.dtos.UserDTO;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class UserDAO {

    public static final String CONST_OF_PROJECT = "constOfProject";
    public static final String USER_IMAGES_FOLDER = "user_images";
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
        data.put("role", userDTO.getRole());
        data.put("status", userDTO.getStatus());
        data.put("photoUri", userDTO.getPhotoUri());

        return doc.update(data);
    }

    public Task<Void> updatePassword (String password) {
        FirebaseUser user = mAuth.getCurrentUser();
        return user.updatePassword(password);
    }

    public Task<Void> deleteUser(String userID) {

        DocumentReference doc = db.collection(COLLECTION_USERS).document(userID);
        Map<String, Object> data = new HashMap<>();
        data.put("status", "deleted");
        return doc.update(data);
    }

    public Task<DocumentSnapshot> getConstOfUser () {
        DocumentReference doc = db.collection(CONST_OF_PROJECT).document("const");
        return doc.get();
    }

    public Task<Uri> uploadImgUserToFirebase(Uri uri) throws Exception{

        StorageReference mStoreRef = FirebaseStorage.getInstance().getReference(USER_IMAGES_FOLDER)
                .child(System.currentTimeMillis() + ".png");
        UploadTask uploadTask = mStoreRef.putFile(uri);
        return uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return mStoreRef.getDownloadUrl();
            }
        });
    }

}
