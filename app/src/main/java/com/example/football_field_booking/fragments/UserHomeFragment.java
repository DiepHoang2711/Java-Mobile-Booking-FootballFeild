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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.football_field_booking.FootballFieldDetailActivity;
import com.example.football_field_booking.R;
import com.example.football_field_booking.SearchActivity;
import com.example.football_field_booking.adapters.FootballFieldAdapter;
import com.example.football_field_booking.daos.FootballFieldDAO;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserHomeFragment extends Fragment {

    private ListView lvFootballField;
    private List<FootballFieldDTO> fieldDTOList;
    private FootballFieldAdapter fieldAdapter;
    private ImageButton imgBtnField5,imgBtnField7,imgBtnField11;

    public UserHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);

        lvFootballField=view.findViewById(R.id.lvFootballField);
        imgBtnField5=view.findViewById(R.id.imgBtnField5);
        imgBtnField7=view.findViewById(R.id.imgBtnField7);
        imgBtnField11=view.findViewById(R.id.imgBtnField11);

        lvFootballField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    FootballFieldDTO dto = (FootballFieldDTO) lvFootballField.getItemAtPosition(i);
                    Intent intent = new Intent(getActivity(), FootballFieldDetailActivity.class);
                    intent.putExtra("fieldID", dto.getFieldID());
                    startActivity(intent);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        imgBtnField5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type="5 people";
                searchByType(type,view);
            }
        });

        imgBtnField7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type="7 people";
                searchByType(type,view);
            }
        });

        imgBtnField11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type="11 people";
                searchByType(type,view);
            }
        });

        return view;
    }

    private void searchByType(String type,View view) {
        Intent intent=new Intent(view.getContext(), SearchActivity.class);
        intent.putExtra("typeField",type);
        startActivity(intent);
    }


    @Override
    public void onStart() {
        super.onStart();
        fieldDTOList = new ArrayList<>();
        FootballFieldDAO dao = new FootballFieldDAO();
        dao.getAllFootballField().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                try {
                    for (QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                        Log.d("USER", "DocID: " + doc.getId());
                        Log.d("USER", "Doc: " + doc.get("fieldInfo", FootballFieldDTO.class));
                        FootballFieldDTO dto = doc.get("fieldInfo", FootballFieldDTO.class);
                        fieldDTOList.add(dto);
                    }
                    fieldAdapter = new FootballFieldAdapter(getActivity(), fieldDTOList);
                    lvFootballField.setAdapter(fieldAdapter);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}