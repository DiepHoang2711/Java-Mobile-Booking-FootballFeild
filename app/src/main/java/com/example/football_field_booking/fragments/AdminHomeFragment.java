package com.example.football_field_booking.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.football_field_booking.R;
import com.example.football_field_booking.adapters.userAdapter;
import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.UserDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeFragment extends Fragment {

    public static final String USER_FOLDER_IMAGES = "user_images";
    private FirebaseStorage storage;
    private ListView listViewUser;

    public AdminHomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_home, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        storage = FirebaseStorage.getInstance();
        listViewUser = getActivity().findViewById(R.id.listViewUser);
        UserDAO userDAO = new UserDAO();
        List<UserDTO> listUser = new ArrayList<>();
        userDAO.getAllUser().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                    listUser.add(doc.toObject(UserDTO.class));
                    Log.d("USER", "Add : " + doc.toObject(UserDTO.class).toString());

                }
                userAdapter adapter = new userAdapter(getActivity(),listUser );
                listViewUser.setAdapter(adapter);
                Log.d("USER", "LIST : " + listUser.toString());
                Log.d("USER", "adapter : " + adapter.getCount());
            }
        });
        Log.d("USER", "Size : " + listUser.size());

        listViewUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

    }

}