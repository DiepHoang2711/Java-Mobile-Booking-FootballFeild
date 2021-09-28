package com.example.football_field_booking.validations;

import com.google.firebase.auth.FirebaseAuth;

public class Validation {
    private FirebaseAuth mAuth;

    public Validation() {
        mAuth = FirebaseAuth.getInstance();
    }

    public boolean isUser (){
        return mAuth.getCurrentUser() != null;
    }
}
