package com.example.football_field_booking.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.football_field_booking.R;
import com.example.football_field_booking.adapters.BookingAdapterOfOwner;
import com.example.football_field_booking.daos.FootballFieldDAO;
import com.example.football_field_booking.dtos.CartItemDTO;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OwnerHomeFragment extends Fragment {

    private List<CartItemDTO> bookingList;
    private ListView lvBooking;
    private BookingAdapterOfOwner adapter;

    public OwnerHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_owner_home, container, false);
        lvBooking=view.findViewById(R.id.lvBooking);

//        loadData();
        return view;
    }

    private void loadData(){
        FootballFieldDAO fieldDAO=new FootballFieldDAO();
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        fieldDAO.getListFieldByOwnerID(user.getUid())
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<CartItemDTO> list=new ArrayList<>();
                for (QueryDocumentSnapshot snapshot:queryDocumentSnapshots){
                    String fieldID= snapshot.getString("fieldInfo.fieldID");
                    fieldDAO.getListBookingByFieldID(fieldID)
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot snapshot1:queryDocumentSnapshots){
                                CartItemDTO cartItemDTO=snapshot.toObject(CartItemDTO.class);
                                list.add(cartItemDTO);
                                adapter=new BookingAdapterOfOwner(getContext());
                                adapter.setListBooking(list);
                                lvBooking.setAdapter(adapter);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
}