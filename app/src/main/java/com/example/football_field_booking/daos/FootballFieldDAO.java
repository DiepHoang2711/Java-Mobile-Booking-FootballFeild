package com.example.football_field_booking.daos;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.football_field_booking.dtos.CartItemDTO;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.example.football_field_booking.dtos.TimePickerDTO;
import com.example.football_field_booking.dtos.UserDTO;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FootballFieldDAO {
    private FirebaseFirestore db;

    private static final String COLLECTION_FOOTBALL_FIELD = "footballFields";
    public static final String SUB_COLLECTION_BOOKING = "booking";
    public static final String FIELD_IMAGES_FOLDER = "football_field_images";
    private static final String COLLECTION_USERS = "users";
    public static final String CONST_OF_PROJECT = "constOfProject";



    public FootballFieldDAO() {
        db = FirebaseFirestore.getInstance();
    }

    public Task<Void> createNewFootballField(FootballFieldDTO fieldDTO,UserDTO owner,List<TimePickerDTO> timePickerDTOList) throws Exception {
        DocumentReference footballFieldReference = db.collection(COLLECTION_FOOTBALL_FIELD).document();
        fieldDTO.setFieldID(footballFieldReference.getId());
        WriteBatch batch= db.batch();
        Map<String, Object> dataFBField = new HashMap<>();
        dataFBField.put("fieldInfo", fieldDTO);
        batch.set(footballFieldReference, dataFBField, SetOptions.merge());

        Map<String, Object> dataOwner= new HashMap<>();
        dataOwner.put("ownerInfo", owner);
        batch.set(footballFieldReference, dataOwner, SetOptions.merge());

        DocumentReference footballFieldInfoReference = db.collection(COLLECTION_USERS).document(owner.getUserID());
        Map<String, Object> dataInOwner = new HashMap<>();
        dataInOwner.put("fieldsInfo", FieldValue.arrayUnion(fieldDTO));
        batch.update(footballFieldInfoReference, dataInOwner);

        Map<String, Object> dataTimePicker= new HashMap<>();
        dataTimePicker.put("timePicker", timePickerDTOList);
        batch.set(footballFieldReference, dataTimePicker, SetOptions.merge());

        return batch.commit();
    }

    public Task<Uri> uploadImgFootballFieldToFirebase(Uri uri) throws Exception {

        StorageReference mStoreRef = FirebaseStorage.getInstance().getReference(FIELD_IMAGES_FOLDER)
                .child(System.currentTimeMillis() + ".png");
        UploadTask uploadTask = mStoreRef.putFile(uri);
        return uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return mStoreRef.getDownloadUrl();
            }
        });
    }

    public Task<DocumentSnapshot> getConstOfFootballField() throws Exception {
        DocumentReference doc = db.collection(CONST_OF_PROJECT).document("const");
        return doc.get();
    }

    public Task<QuerySnapshot> getAllFootballField() {
        return db.collection(COLLECTION_FOOTBALL_FIELD).get();
    }

    public Task<DocumentSnapshot> getFieldByID (String fieldID) {
        DocumentReference doc = db.collection(COLLECTION_FOOTBALL_FIELD).document(fieldID);
        return doc.get();
    }

    public Task<Void> updateFootballField (FootballFieldDTO fieldDTO, FootballFieldDTO fieldOldDTO, String userID, List<TimePickerDTO> timePickerDTOList) throws Exception {
        DocumentReference docField = db.collection(COLLECTION_FOOTBALL_FIELD).document(fieldDTO.getFieldID());
        DocumentReference docOwner = db.collection(COLLECTION_USERS).document(userID);

        Log.d("USER", "docFieldOfOwner: " + docOwner);
        return db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                Map<String, Object> dataField = new HashMap<>();
                dataField.put("fieldInfo.name", fieldDTO.getName());
                dataField.put("fieldInfo.location", fieldDTO.getLocation());
                dataField.put("fieldInfo.type", fieldDTO.getType());
                dataField.put("fieldInfo.image", fieldDTO.getImage());
                dataField.put("fieldInfo.status", fieldDTO.getStatus());
                transaction.update(docField, dataField);

                Map<String,Object> dataDeleteField = new HashMap<>();
                dataDeleteField.put("fieldsInfo", FieldValue.arrayRemove(fieldOldDTO));
                transaction.update(docOwner, dataDeleteField);

                Map<String,Object> dataUpdateField = new HashMap<>();
                dataUpdateField.put("fieldsInfo", FieldValue.arrayUnion(fieldDTO));
                transaction.update(docOwner, dataUpdateField);

                Map<String,Object> dataDeleteTimePicker = new HashMap<>();
                dataDeleteTimePicker.put("timePicker", FieldValue.delete());
                transaction.update(docField, dataDeleteTimePicker);

                Map<String, Object> dataUpdateTimePicker = new HashMap<>();
                dataUpdateTimePicker.put("timePicker", timePickerDTOList);
                transaction.set(docField, dataUpdateTimePicker, SetOptions.merge());

                return null;
            }
        });
    }

    public Task<QuerySnapshot> searchByLikeName(String name) throws Exception{
        return db.collection(COLLECTION_FOOTBALL_FIELD).whereGreaterThanOrEqualTo("name",name).get();
    }

    public Task<QuerySnapshot> getBookingByFieldAndDate (List<CartItemDTO> cart) {
        List<String> listFieldAndDate = new ArrayList<>();
        for (CartItemDTO cartItemDTO: cart) {
            listFieldAndDate.add(cartItemDTO.getFieldInfo().getFieldID() + cartItemDTO.getDate());
        }
        return db.collectionGroup(SUB_COLLECTION_BOOKING).whereIn("fieldAndDate", listFieldAndDate).get();
    }

    public Task<QuerySnapshot> getBookingOfAFieldByDate (String fieldID, String date) {
        return db.collection(COLLECTION_FOOTBALL_FIELD).document(fieldID)
                .collection(SUB_COLLECTION_BOOKING).whereEqualTo("date", date).get();
    }


}
