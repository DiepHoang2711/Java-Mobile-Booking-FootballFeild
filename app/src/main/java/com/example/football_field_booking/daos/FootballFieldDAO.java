package com.example.football_field_booking.daos;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.example.football_field_booking.dtos.TimePickerDTO;
import com.example.football_field_booking.dtos.UserDTO;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FootballFieldDAO {
    private FirebaseFirestore db;

    private static final String COLLECTION_FOOTBALL_FIELD = "footballFields";

    public static final String FIELD_IMAGES_FOLDER = "football_field_images";

    public static final String SUB_COLLECTION_FOOTBALL_FIELD_INFO = "footballFieldInfos";

    public static final String SUB_COLLECTION_TIME_PICKER="timePicker";

    private static final String SUB_COLLECTION_OWNER_INFO = "ownerInfo";

    private static final String COLLECTION_USERS = "users";

    public static final String CONST_OF_PROJECT = "constOfProject";



    public FootballFieldDAO() {
        db = FirebaseFirestore.getInstance();
    }

    public Task<Void> createNewFootballField(FootballFieldDTO fieldDTO, Uri uriImg,UserDTO owner,List<TimePickerDTO> timePickerDTOList) throws Exception {
        DocumentReference footballFieldReference = db.collection(COLLECTION_FOOTBALL_FIELD).document();
        fieldDTO.setFieldID(footballFieldReference.getId());
        WriteBatch batch= db.batch();
        batch.set(footballFieldReference,fieldDTO);

        DocumentReference ownerInfoReference = footballFieldReference.collection(SUB_COLLECTION_OWNER_INFO).document(owner.getUserID());
        batch.set(ownerInfoReference,owner);

        DocumentReference footballFieldInfoReference = db.collection(COLLECTION_USERS).document(owner.getUserID()).collection(SUB_COLLECTION_FOOTBALL_FIELD_INFO).document(fieldDTO.getFieldID());
        batch.set(footballFieldInfoReference,fieldDTO);

        for (TimePickerDTO dto:timePickerDTOList){
            DocumentReference reference=  footballFieldReference.collection(SUB_COLLECTION_TIME_PICKER).document();
            dto.setTimePickerID(reference.getId());
            batch.set(reference,dto);
        }
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

    public Task<DocumentSnapshot> getTypeOfFootballField() throws Exception {
        DocumentReference doc = db.collection(CONST_OF_PROJECT).document("const");
        return doc.get();
    }

    public Task<QuerySnapshot> getAllFootballFieldOfOwner(String ownerID) {
        return db.collection(COLLECTION_USERS).document(ownerID).collection(SUB_COLLECTION_FOOTBALL_FIELD_INFO).get();
    }

    public Task<DocumentSnapshot> getFieldByID (String fieldID) {
        DocumentReference doc = db.collection(COLLECTION_FOOTBALL_FIELD).document(fieldID);
        return doc.get();
    }

    public Task<QuerySnapshot> getAllTimePickerOfField (String fieldID) {
        return db.collection(COLLECTION_FOOTBALL_FIELD).document(fieldID).collection(SUB_COLLECTION_TIME_PICKER).get();
    }

    public Task<Void> updateFootballField (FootballFieldDTO fieldDTO, List<TimePickerDTO> listTimePicker) throws Exception {
        DocumentReference docField = db.collection(COLLECTION_FOOTBALL_FIELD).document(fieldDTO.getFieldID());
        DocumentReference docFieldOfOwner = docField.getParent().getParent();
        return db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                Map<String, Object> dataField = new HashMap<>();
                dataField.put("name", fieldDTO.getName());
                dataField.put("location", fieldDTO.getLocation());
                dataField.put("type", fieldDTO.getType());
                dataField.put("image", fieldDTO.getImage());
                dataField.put("status", fieldDTO.getStatus());
                transaction.update(docField, dataField);

                transaction.update(docFieldOfOwner, dataField);

                return null;
            }
        });
    }

    public void updateTimePicker (List<TimePickerDTO> list, String fieldID) throws Exception {
        DocumentReference docField = db.collection(COLLECTION_FOOTBALL_FIELD).document(fieldID);

    }
}
