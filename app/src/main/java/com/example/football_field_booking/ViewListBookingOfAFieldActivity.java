package com.example.football_field_booking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.football_field_booking.adapters.BookingAdapterOfAField;
import com.example.football_field_booking.daos.FootballFieldDAO;
import com.example.football_field_booking.dtos.CartItemDTO;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewListBookingOfAFieldActivity extends AppCompatActivity {

    private List<CartItemDTO> bookingList;
    private ListView lvBooking;
    private BookingAdapterOfAField adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list_booking_of_a_field);
        lvBooking=findViewById(R.id.lvBooking);

        Intent intent = getIntent();
        String fieldID = intent.getStringExtra("fieldID");
        if (fieldID != null) {
            FootballFieldDAO fieldDAO=new FootballFieldDAO();
            fieldDAO.getListBookingByFieldID(fieldID)
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            bookingList=new ArrayList<>();
                            List<DocumentSnapshot> documentSnapshotList=queryDocumentSnapshots.getDocuments();
                            for (int i=documentSnapshotList.size()-1;i>=0;i--){
                                CartItemDTO cartItemDTO=documentSnapshotList.get(i).toObject(CartItemDTO.class);
                                bookingList.add(cartItemDTO);
                            }
                            Log.e("LIST BOOKING",bookingList.size()+"");
                            adapter =new BookingAdapterOfAField(ViewListBookingOfAFieldActivity.this);
                            adapter.setListBooking(bookingList);
                            lvBooking.setAdapter(adapter);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    public void clickToBack(View view) {
        finish();
    }
}