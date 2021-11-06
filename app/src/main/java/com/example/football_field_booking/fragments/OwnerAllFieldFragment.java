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
import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.example.football_field_booking.dtos.UserDocument;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OwnerAllFieldFragment extends Fragment {

    private ExtendedFloatingActionButton btnCreate;
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
        UserDAO dao = new UserDAO();
        dao.getUserById(user.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                try {
                    List<FootballFieldDTO> fieldDTOList = documentSnapshot.toObject(UserDocument.class).getFieldsInfo();
                    Log.d("USER", "dto: " + fieldDTOList);
                    if(fieldDTOList != null) {
                        FootballFieldAdapter fieldAdapter = new FootballFieldAdapter(getContext(), fieldDTOList);
                        lvFootballFieldOwner.setAdapter(fieldAdapter);
                        fieldAdapter.notifyDataSetChanged();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}