package com.example.football_field_booking.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.football_field_booking.R;
import com.example.football_field_booking.AFieldDetailForOwnerActivity;
import com.example.football_field_booking.adapters.FootballFieldAdapter;
import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.example.football_field_booking.dtos.UserDocument;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class OwnerHomeFragment extends Fragment {

    private ListView lvFootballFieldOwner;

    public OwnerHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_owner_home, container, false);
        lvFootballFieldOwner=view.findViewById(R.id.lvFootballFieldOwner);
        loadData();
        return view;
    }

    private void loadData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserDAO dao = new UserDAO();
        dao.getUserById(user.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                try {
                    List<FootballFieldDTO> fieldDTOList = documentSnapshot.toObject(UserDocument.class).getFieldsInfo();
                    if (fieldDTOList != null) {
                        FootballFieldAdapter fieldAdapter = new FootballFieldAdapter(getActivity(), fieldDTOList);
                        lvFootballFieldOwner.setAdapter(fieldAdapter);
                        fieldAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        lvFootballFieldOwner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    FootballFieldDTO dto = (FootballFieldDTO) lvFootballFieldOwner.getItemAtPosition(i);
                    Intent intent = new Intent(getActivity(), AFieldDetailForOwnerActivity.class);
                    intent.putExtra("fieldID", dto.getFieldID());
                    intent.putExtra("fieldName",dto.getName());
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}