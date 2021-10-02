package com.example.football_field_booking.daos;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FootballFieldDAO {
    private FirebaseFirestore db;

    private static final String COLLECTION_FOOTBALL_FIELD = "footballFields";

    private FootballFieldDTO footballFieldDTO;

    private List<FootballFieldDTO> fieldDTOList;

    public static final String FOOTBALL_FIELD_DAO_LOG = "football_field_dao";

    public FootballFieldDAO() {
        db = FirebaseFirestore.getInstance();
    }

    public Task<DocumentReference> createNewFootballField(FootballFieldDTO fieldDTO){
        try{

            return db.collection(COLLECTION_FOOTBALL_FIELD).add(fieldDTO);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }



}
