package com.example.football_field_booking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.football_field_booking.adapters.BookingAdapterOfAField;
import com.example.football_field_booking.adapters.TabLayoutFragmentAdapter;
import com.example.football_field_booking.daos.FootballFieldDAO;
import com.example.football_field_booking.dtos.CartItemDTO;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class AFieldDetailForOwnerActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private TextView txtFieldName;
    private ViewPager2 viewPager2;
    private TabLayoutFragmentAdapter fragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_field_detail_for_owner);
        txtFieldName = findViewById(R.id.txtFieldName);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager2);

        Intent intent = getIntent();
        String fieldName = intent.getStringExtra("fieldName");
        String fieldID = intent.getStringExtra("fieldID");
        if (fieldName != null) {
            txtFieldName.setText(fieldName);
        } else {
            FootballFieldDAO fieldDAO = new FootballFieldDAO();
            fieldDAO.getFieldByID(fieldID)
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String name=documentSnapshot.getString("fieldInfo.name");
                            txtFieldName.setText(name);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentAdapter = new TabLayoutFragmentAdapter(fragmentManager, getLifecycle());

        if (fieldID != null) {
            Bundle bundle = new Bundle();
            bundle.putString("fieldID", fieldID);
            fragmentAdapter.setBundle(bundle);
        }
        viewPager2.setAdapter(fragmentAdapter);

        tabLayout.addTab(tabLayout.newTab().setText("Booking List"));
        tabLayout.addTab(tabLayout.newTab().setText("Rate and Comment"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }

    public void clickToBack(View view) {
        finish();
    }
}