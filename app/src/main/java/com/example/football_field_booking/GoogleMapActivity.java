package com.example.football_field_booking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.example.football_field_booking.fragments.MapChooseLocationFragment;

public class GoogleMapActivity extends AppCompatActivity {

    public static final int RC_PERMISSTION_LOCATION = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, RC_PERMISSTION_LOCATION);
        } else {
            loadData();
        }

    }

    private void loadData () {
        Intent intent = this.getIntent();
        String action = intent.getStringExtra("action");

        if (action.equals("chooseLocation")){
            MapChooseLocationFragment fragment = new MapChooseLocationFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.layoutGoogleMap, fragment).commit();
        }else {
            Toast.makeText(this, getResources().getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT).show();
            this.setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == RC_PERMISSTION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadData();
            } else {
                Toast.makeText(this, "Can not open google map", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}