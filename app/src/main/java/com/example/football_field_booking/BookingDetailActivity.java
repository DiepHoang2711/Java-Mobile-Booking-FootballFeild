package com.example.football_field_booking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.football_field_booking.adapters.BookingDetailAdapter;
import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.BookingDetailDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BookingDetailActivity extends AppCompatActivity {

    private TextView txtTotal;
    private ListView lvBookingDetail;
    private BookingDetailAdapter bookingDetailAdapter;
    private String bookingID;
    private float total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);
        txtTotal = findViewById(R.id.txtTotal);
        lvBookingDetail = findViewById(R.id.lvBookingDetail);
        bookingDetailAdapter = new BookingDetailAdapter(this);

        Intent intent = this.getIntent();
        bookingID = intent.getStringExtra("bookingID");
        total = intent.getFloatExtra("total", -1);
        if(bookingID == null || total == -1) {
            Toast.makeText(this, getResources().getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        txtTotal.setText("$"+total);

        loadListData();

    }

    private void loadListData() {
        UserDAO userDAO = new UserDAO();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            userDAO.getAllBookingDetail(user.getUid(), bookingID).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        List<BookingDetailDTO> list = new ArrayList<>();
                        for (QueryDocumentSnapshot doc: task.getResult()) {
                            BookingDetailDTO dto = doc.toObject(BookingDetailDTO.class);
                            list.add(dto);
                        }
                        bookingDetailAdapter.setBooking(list);
                        bookingDetailAdapter.setBookingID(bookingID);
                        lvBookingDetail.setAdapter(bookingDetailAdapter);
                    }else {
                        task.getException().printStackTrace();
                    }
                }
            });
        }

    }
}