package com.example.football_field_booking.Validation;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Validation {
    private FirebaseAuth mAuth;

    public Validation() {
        mAuth = FirebaseAuth.getInstance();
    }

    public boolean isUser (){
        FirebaseUser user = mAuth.getCurrentUser();
        return user != null && user.isEmailVerified();
    }
}
