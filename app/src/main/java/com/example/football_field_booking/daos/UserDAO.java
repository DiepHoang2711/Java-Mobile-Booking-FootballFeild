package com.example.football_field_booking.daos;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.football_field_booking.dtos.CartItemDTO;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.example.football_field_booking.dtos.TimePickerDTO;
import com.example.football_field_booking.dtos.UserDTO;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAO {

    public static final String CONST_OF_PROJECT = "constOfProject";
    public static final String USER_IMAGES_FOLDER = "user_images";
    public static final String SUB_COLLECTION_CART = "cart";
    public static final String SUB_COLLECTION_TIMESLOTS = "timeSlots";
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

    public Task<Void> addToCart (CartItemDTO cartItemDTO, String userID) {
        WriteBatch batch = db.batch();

        DocumentReference docField = db.collection(COLLECTION_USERS).document(userID).collection(SUB_COLLECTION_CART).document();
        Map<String, Object> data = new HashMap<>();
        data.put("fieldID", cartItemDTO.getFieldID());
        data.put("name", cartItemDTO.getName());
        data.put("location", cartItemDTO.getLocation());
        data.put("type", cartItemDTO.getType());
        data.put("image", cartItemDTO.getImage());
        data.put("date", cartItemDTO.getDate());
        data.put("total", cartItemDTO.getTotal());

        batch.set(docField, data);
        for (TimePickerDTO dto: cartItemDTO.getTimePickerDTOList()) {
            DocumentReference docTimePicker = docField.collection(SUB_COLLECTION_TIMESLOTS).document();
            dto.setTimePickerID(docTimePicker.getId());
            batch.set(docTimePicker, dto);
        }

        return batch.commit();
    }

}
