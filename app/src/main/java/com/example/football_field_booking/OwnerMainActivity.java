package com.example.football_field_booking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.football_field_booking.fragments.OwnerAllFieldFragment;
import com.example.football_field_booking.fragments.OwnerHomeFragment;
import com.example.football_field_booking.fragments.ProfileFragment;
import com.example.football_field_booking.validations.Validation;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class OwnerMainActivity extends AppCompatActivity {

    private MaterialToolbar topAppBar;
    private BottomNavigationView bottomNavigationView;

    private Validation validation = new Validation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_main);

        topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setTitleTextAppearance(this, R.style.FontLogo);
        View logo = topAppBar.getChildAt(0);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        Intent intent=getIntent();
        String action=intent.getStringExtra("action");
        if (action != null) {
            if (action.equals("view_my_field")) {
                Fragment fragment = new OwnerAllFieldFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
                bottomNavigationView.setSelectedItemId(R.id.pageField);
            }
        } else {
            String fieldID=intent.getStringExtra("fieldID");
            if(fieldID!=null){
                Intent intent1=new Intent(OwnerMainActivity.this, AFieldDetailForOwnerActivity.class);
                intent1.putExtra("fieldID",fieldID);
                startActivity(intent1);
            }else{
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new OwnerHomeFragment()).commit();
            }
        }


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()){
                    case R.id.pageHome:
                        selectedFragment = new OwnerHomeFragment();
                        break;
                    case R.id.pageField:
                        selectedFragment = new OwnerAllFieldFragment();
                        break;
                    case R.id.pageAccount:
                        selectedFragment = new ProfileFragment();
                        break;
                    default:
                        return false;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();
                return true;
            }
        });



        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OwnerMainActivity.this, OwnerMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });



    }
}