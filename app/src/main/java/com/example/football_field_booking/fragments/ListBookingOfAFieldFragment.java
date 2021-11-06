package com.example.football_field_booking.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.football_field_booking.R;
import com.example.football_field_booking.adapters.BookingAdapterOfAField;
import com.example.football_field_booking.daos.FootballFieldDAO;
import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.CartItemDTO;
import com.example.football_field_booking.dtos.UserDTO;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class ListBookingOfAFieldFragment extends Fragment {

    private List<CartItemDTO> bookingList;
    private ListView lvBooking;
    private BookingAdapterOfAField adapter;
    private List<String> timeBookingAtList;

    public ListBookingOfAFieldFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_booking_of_a_field, container, false);
        lvBooking = view.findViewById(R.id.lvBooking);
        Bundle bundle = getArguments();
        String fieldID = bundle.getString("fieldID");
        if (fieldID != null) {
            FootballFieldDAO fieldDAO = new FootballFieldDAO();
            fieldDAO.getListBookingByFieldID(fieldID)
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            bookingList = new ArrayList<>();
                            timeBookingAtList = new ArrayList<>();
                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                CartItemDTO cartItemDTO = snapshot.toObject(CartItemDTO.class);
                                String bookingAt = snapshot.getString("bookingAt");
                                bookingList.add(cartItemDTO);
                                timeBookingAtList.add(bookingAt);
                            }
                            adapter = new BookingAdapterOfAField(getContext());
                            adapter.setListBooking(bookingList);
                            adapter.setTimeBookingAtList(timeBookingAtList);
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
        return view;
    }
}