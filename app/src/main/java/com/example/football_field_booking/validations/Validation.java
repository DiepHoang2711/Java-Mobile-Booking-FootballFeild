package com.example.football_field_booking.validations;

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

    public boolean isEmpty (String text) {
        return text.trim().isEmpty();
    }

    public boolean isValidPassword (String password) {
        return !password.trim().isEmpty() && password.length() > 8;
    }

    public boolean isValidPhoneNumber (String phoneNumber) {
        boolean result = false;
        try {
            Integer.parseInt(phoneNumber);
            if (phoneNumber.trim().isEmpty() || phoneNumber.length() > 11 || phoneNumber.length() < 8) {
                result = false;
            }else {
                result = true;
            }
        }catch (Exception e) {
            result = false;
        }
        return result;
    }

    public boolean isValidEmail ( String email) {
        return !email.trim().isEmpty() && email.contains("@");
    }

}
