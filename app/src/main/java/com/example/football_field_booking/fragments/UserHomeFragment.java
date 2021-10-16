package com.example.football_field_booking.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.football_field_booking.CreateFootballFieldActivity;
import com.example.football_field_booking.R;
import com.example.football_field_booking.UpdateFootballFieldActivity;
import com.example.football_field_booking.adapters.FootballFieldAdapter;
import com.example.football_field_booking.daos.FootballFieldDAO;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserHomeFragment extends Fragment {

    private ListView lvFootballField;
    private List<FootballFieldDTO> fieldDTOList;
    private FootballFieldAdapter fieldAdapter;

    public UserHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);

        lvFootballField=view.findViewById(R.id.lvFootballField);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        fieldDTOList = new ArrayList<>();


        FootballFieldDAO dao = new FootballFieldDAO();
        dao.getAllFootballField().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                try {
                    for (QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                        Log.d("USER", "DocID: " + doc.getId());
                        Log.d("USER", "Doc: " + doc.toObject(FootballFieldDTO.class));
                        fieldDTOList.add(doc.toObject(FootballFieldDTO.class));
                    }
                    fieldAdapter = new FootballFieldAdapter(getActivity(), fieldDTOList);
                    lvFootballField.setAdapter(fieldAdapter);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        lvFootballField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    FootballFieldDTO dto = (FootballFieldDTO) lvFootballField.getItemAtPosition(i);
                    Intent intent = new Intent(getActivity(), UpdateFootballFieldActivity.class);
                    intent.putExtra("fieldID", dto.getFieldID());
                    startActivity(intent);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }
}