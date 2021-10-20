package com.example.football_field_booking.daos;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.football_field_booking.dtos.CartItemDTO;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.example.football_field_booking.dtos.TimePickerDTO;
import com.example.football_field_booking.dtos.UserDTO;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAO {

    private static final String COLLECTION_FOOTBALL_FIELD = "footballFields";
    public static final String CONST_OF_PROJECT = "constOfProject";
    public static final String USER_IMAGES_FOLDER = "user_images";
    public static final String SUB_COLLECTION_CART = "cart";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private static final String COLLECTION_USERS = "users";

    public UserDAO() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public Task<Void> createUser(UserDTO userDTO) {
        DocumentReference doc = db.collection(COLLECTION_USERS).document(userDTO.getUserID());
        Map<String, Object> data = new HashMap<>();
        data.put("userInfo", userDTO);
        return doc.set(data);
    }


    public Task<DocumentSnapshot> getUserById(String id){
        DocumentReference doc = db.collection(COLLECTION_USERS).document(id);
        return doc.get();
    }

    public Task<QuerySnapshot> getAllUser() {
        return db.collection(COLLECTION_USERS).get();
    }

    public Task<Void> updateUser(UserDTO userDTO, List<FootballFieldDTO> list) {
        WriteBatch batch = db.batch();

        DocumentReference docUser = db.collection(COLLECTION_USERS).document(userDTO.getUserID());
        Map<String, Object> dataUser = new HashMap<>();
        dataUser.put("userInfo.email", userDTO.getEmail());
        dataUser.put("userInfo.fullName", userDTO.getFullName());
        dataUser.put("userInfo.phone", userDTO.getPhone());
        dataUser.put("userInfo.role", userDTO.getRole());
        dataUser.put("userInfo.status", userDTO.getStatus());
        dataUser.put("userInfo.photoUri", userDTO.getPhotoUri());

        batch.update(docUser, dataUser);

        if (userDTO.getRole().equals("owner") && list != null) {

            for (FootballFieldDTO dto: list) {
                DocumentReference doc = db.collection(COLLECTION_FOOTBALL_FIELD).document(dto.getFieldID());
                Map<String, Object> dataInFBfield = new HashMap<>();
                dataInFBfield.put("ownerInfo.email", userDTO.getEmail());
                dataInFBfield.put("ownerInfo.fullName", userDTO.getFullName());
                dataInFBfield.put("ownerInfo.phone", userDTO.getPhone());
                dataInFBfield.put("ownerInfo.role", userDTO.getRole());
                dataInFBfield.put("ownerInfo.status", userDTO.getStatus());
                dataInFBfield.put("ownerInfo.photoUri", userDTO.getPhotoUri());

                batch.update(doc, dataInFBfield);
            }
        }
        return batch.commit();

    }

    public Task<Void> updatePassword (String password) {
        FirebaseUser user = mAuth.getCurrentUser();
        return user.updatePassword(password);
    }

    public Task<Void> deleteUser(String userID) {

        DocumentReference doc = db.collection(COLLECTION_USERS).document(userID);
        Map<String, Object> data = new HashMap<>();
        data.put("userInfo.status", "deleted");
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
//        return docField.set(cartItemDTO);
//        Map<String, Object> data = new HashMap<>();
//        data.put("fieldID", cartItemDTO.getFieldID());
//        data.put("name", cartItemDTO.getName());
//        data.put("location", cartItemDTO.getLocation());
//        data.put("type", cartItemDTO.getType());
//        data.put("image", cartItemDTO.getImage());
//        data.put("date", cartItemDTO.getDate());
//        data.put("total", cartItemDTO.getTotal());

//        batch.set(docField, docField);
//        for (TimePickerDTO dto: cartItemDTO.getTimePickerDTOList()) {
//            DocumentReference docTimePicker = docField.collection(SUB_COLLECTION_TIMESLOTS).document();
//            dto.setTimePickerID(docTimePicker.getId());
//            batch.set(docTimePicker, dto);
//        }
//
        batch.set(docField, cartItemDTO);
        return batch.commit();
    }

    public Task<QuerySnapshot> getItemInCartByDateAndFieldID (String userID, String fieldID, String date) {
        return db.collection(COLLECTION_USERS).document(userID).collection(SUB_COLLECTION_CART)
                .whereEqualTo("date", date).whereEqualTo("fieldID", fieldID).get();

    }

}
