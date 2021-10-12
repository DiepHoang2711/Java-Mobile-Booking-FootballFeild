package com.example.football_field_booking.daos;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.example.football_field_booking.dtos.UserDTO;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;
import java.util.Map;

public class FootballFieldDAO {
    private FirebaseFirestore db;

    private static final String COLLECTION_FOOTBALL_FIELD = "footballFields";

    public static final String FIELD_IMAGES_FOLDER = "football_field_images";

    public static final String SUB_COLLECTION_FOOTBALL_FIELD_INFO="footballFiledInfos";

    private static final String SUB_COLLECTION_OWNER_INFO= "ownerInfo";

    private static final String COLLECTION_USERS = "users";

    public static final String CONST_OF_PROJECT = "constOfProject";


    public FootballFieldDAO() {
        db = FirebaseFirestore.getInstance();
    }

    public Task<DocumentReference> createNewFootballField(FootballFieldDTO fieldDTO, Uri uriImg) throws Exception{
            DocumentReference reference=db.collection(COLLECTION_FOOTBALL_FIELD).document();
            fieldDTO.setFieldID(reference.getId());
            fieldDTO.setImage(uploadImgFootballFieldToFirebase(uriImg).getResult().toString());
            return db.collection(COLLECTION_FOOTBALL_FIELD).add(fieldDTO);
    }

    public Task<DocumentReference> addOwnerToFootballFieldsCollection(UserDTO owner,DocumentReference footballFIeldReference) throws Exception{
        DocumentReference reference=footballFIeldReference.collection(SUB_COLLECTION_OWNER_INFO).document(owner.getUserID());
        return reference.getParent().add(owner);
    }

    public Task<DocumentReference> addFootFiledInfoToUsersCollection(String ownerID,FootballFieldDTO fieldDTO) throws Exception{
        DocumentReference reference=db.collection(COLLECTION_USERS).document(ownerID).collection(SUB_COLLECTION_FOOTBALL_FIELD_INFO).document(fieldDTO.getFieldID());
        return reference.getParent().add(fieldDTO);
    }


    public Task<Uri> uploadImgFootballFieldToFirebase(Uri uri) throws Exception{

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

    public Task<DocumentSnapshot> getTypeOfFootballField () throws Exception {
        DocumentReference doc = db.collection(CONST_OF_PROJECT).document("const");
        return doc.get();
    }
}
