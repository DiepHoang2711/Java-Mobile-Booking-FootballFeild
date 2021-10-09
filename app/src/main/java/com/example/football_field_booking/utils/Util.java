package com.example.football_field_booking.utils;

import android.app.ProgressDialog;

import com.google.android.material.textfield.TextInputLayout;

public class Util {
    public Util() {
    }

    public void showError(TextInputLayout input, String textError) {
        input.setErrorEnabled(true);
        input.setError(textError);
        input.requestFocus();
    }

    public void clearError(TextInputLayout input){
        input.setErrorEnabled(false);
        input.setError(null);
    }

    public void showProgressDialog(ProgressDialog dialog, String title, String message){
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
