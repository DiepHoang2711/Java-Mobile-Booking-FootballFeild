package com.example.football_field_booking.Validation;

import com.google.firebase.auth.FirebaseAuth;

public class Validation {
    private FirebaseAuth mAuth;

    public Validation() {
        mAuth = FirebaseAuth.getInstance();
    }

    private boolean isUser (){
        return mAuth.getCurrentUser() != null;
    }
}
