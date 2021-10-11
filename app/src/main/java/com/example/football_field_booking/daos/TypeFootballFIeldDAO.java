package com.example.football_field_booking.daos;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TypeFootballFIeldDAO {

    private FirebaseFirestore db;
    public static final String CONST_OF_PROJECT = "constOfProject";

    public TypeFootballFIeldDAO() {
        this.db = FirebaseFirestore.getInstance();
    }

    public Task<DocumentSnapshot> getTypeOfFootballField () throws Exception {
        DocumentReference doc = db.collection(CONST_OF_PROJECT).document("const");
        return doc.get();
    }
}
