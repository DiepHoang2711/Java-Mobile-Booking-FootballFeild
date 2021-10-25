package com.example.football_field_booking.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.football_field_booking.BookingDetailActivity;
import com.example.football_field_booking.R;
import com.example.football_field_booking.adapters.BookingAdapter;
import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.BookingDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private ListView lvBooking;
    private BookingAdapter bookingAdapter;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        lvBooking = view.findViewById(R.id.lvBooking);
        bookingAdapter = new BookingAdapter(getActivity());
        Log.d("USER", "hello history fragment: ");

        lvBooking.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BookingDTO dto = (BookingDTO) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(getActivity(), BookingDetailActivity.class);
                intent.putExtra("bookingID", dto.getBookingID());
                intent.putExtra("total", dto.getTotal());
                startActivity(intent);
            }
        });
        loadData();

        return view;
    }

    private void loadData() {
        UserDAO userDAO = new UserDAO();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            userDAO.getAllBooking(user.getUid()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        List<BookingDTO> list = new ArrayList<>();
                        Log.d("USER", "hello history fragment: ");
                        for (QueryDocumentSnapshot doc: task.getResult()) {
                            BookingDTO dto = doc.toObject(BookingDTO.class);
                            Log.d("USER", "dto: " + dto.toString());
                            list.add(dto);
                        }
                        bookingAdapter.setListBooking(list);
                        lvBooking.setAdapter(bookingAdapter);
                    }else {
                        task.getException().printStackTrace();
                    }
                }
            });
        }else {
            Toast.makeText(getActivity(), getResources().getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT).show();
        }

    }
}