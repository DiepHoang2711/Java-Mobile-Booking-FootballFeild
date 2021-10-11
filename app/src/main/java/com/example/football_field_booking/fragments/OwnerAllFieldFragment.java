package com.example.football_field_booking.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.football_field_booking.CreateFootballFieldActivity;
import com.example.football_field_booking.R;
import com.example.football_field_booking.adapters.FootballFieldAdapter;
import com.example.football_field_booking.dtos.FootballFieldDTO;

import java.util.List;

public class OwnerAllFieldFragment extends Fragment {

    private Button btnCreate;
    private ListView lvFootballFieldOwner;
    private FootballFieldAdapter fieldAdapter;
    private List<FootballFieldDTO> fieldDTOList;

    public OwnerAllFieldFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_owner_all_field, container, false);

        btnCreate=view.findViewById(R.id.btnCreateFootballField);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), CreateFootballFieldActivity.class);
                startActivity(intent);
            }
        });

        lvFootballFieldOwner=view.findViewById(R.id.lvFootballFieldOwner);

        return view;
    }
    private void loadData(){


//        fieldAdapter=new FootballFieldAdapter(OwnerHomeActivity.this,)
    }

}