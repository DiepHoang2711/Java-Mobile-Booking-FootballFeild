package com.example.football_field_booking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.football_field_booking.fragments.AdminHomeFragment;
import com.example.football_field_booking.fragments.ProfileFragment;
import com.example.football_field_booking.validations.Validation;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminMainActivity extends AppCompatActivity {

    private MaterialToolbar topAppBar;

    private Validation validation = new Validation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setTitleTextAppearance(this, R.style.FontLogo);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new AdminHomeFragment()).commit();
    }

    public void clickToGoToSearchActivity(MenuItem item) {
        Intent intent=new Intent(this, SearchFieldActivity.class);
        startActivity(intent);
    }

    public void clickToViewProfile(MenuItem item) {
        topAppBar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24));
        topAppBar.setTitle("Profile");
        topAppBar.setLogo(null);
        topAppBar.setTitleCentered(true);
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new AdminHomeFragment()).commit();
                topAppBar.setNavigationIcon(null);
                topAppBar.setLogo(getResources().getDrawable(R.drawable.logo));
                topAppBar.setTitle("Fast Field");
            }
        });

        ProfileFragment profileFragment;
        if (validation.isUser()) {
            profileFragment = new ProfileFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, profileFragment).commit();
        } else {
            Intent intent = new Intent(AdminMainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

    }
}