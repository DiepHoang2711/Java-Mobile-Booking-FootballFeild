package com.example.football_field_booking.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.football_field_booking.CreateFootballFieldActivity;
import com.example.football_field_booking.R;
import com.example.football_field_booking.EditFootballFieldActivity;
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

public class OwnerAllFieldFragment extends Fragment {

    private Button btnCreate;
    private ListView lvFootballFieldOwner;
//    private FootballFieldAdapter fieldAdapter;
//    private List<FootballFieldDTO> fieldDTOList;

    public OwnerAllFieldFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_owner_all_field, container, false);

        btnCreate=view.findViewById(R.id.btnCreateFootballField);
        lvFootballFieldOwner=view.findViewById(R.id.lvFootballFieldOwner);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        List<FootballFieldDTO> fieldDTOList = new ArrayList<>();

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), CreateFootballFieldActivity.class);
                startActivity(intent);
            }
        });

        lvFootballFieldOwner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    FootballFieldDTO dto = (FootballFieldDTO) lvFootballFieldOwner.getItemAtPosition(i);
                    Intent intent = new Intent(getActivity(), EditFootballFieldActivity.class);
                    intent.putExtra("fieldID", dto.getFieldID());
                    startActivity(intent);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        List<FootballFieldDTO> fieldDTOList = new ArrayList<>();
        FootballFieldDAO dao = new FootballFieldDAO();
        dao.getAllFootballFieldOfOwner(user.getUid()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                try {
                    for (QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                        Log.d("USER", "DocID: " + doc.getId());
                        Log.d("USER", "Doc: " + doc.toObject(FootballFieldDTO.class));
                        fieldDTOList.add(doc.toObject(FootballFieldDTO.class));
                    }
                    Log.d("USER", "dto: " + fieldDTOList);

                    FootballFieldAdapter fieldAdapter = new FootballFieldAdapter(getActivity(), fieldDTOList);
                    lvFootballFieldOwner.setAdapter(fieldAdapter);
                    fieldAdapter.notifyDataSetChanged();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}