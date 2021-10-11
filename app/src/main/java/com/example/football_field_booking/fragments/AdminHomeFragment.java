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

import com.example.football_field_booking.CreateUserActivity;
import com.example.football_field_booking.ProfileEditByAdminActivity;
import com.example.football_field_booking.R;
import com.example.football_field_booking.adapters.UserAdapter;
import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.UserDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeFragment extends Fragment {

    private ListView listViewUser;
    private Button btnCreateUser;

    public AdminHomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        listViewUser = view.findViewById(R.id.listViewUser);
        btnCreateUser = view.findViewById(R.id.btnCreateUser);

        UserDAO userDAO = new UserDAO();
        List<UserDTO> listUser = new ArrayList<>();
        userDAO.getAllUser().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                    listUser.add(doc.toObject(UserDTO.class));
                    Log.d("USER", "Add : " + doc.toObject(UserDTO.class).toString());

                }
                UserAdapter adapter = new UserAdapter(getActivity(),listUser );
                listViewUser.setAdapter(adapter);
                Log.d("USER", "LIST : " + listUser.toString());
                Log.d("USER", "adapter : " + adapter.getCount());
            }
        });
        Log.d("USER", "Size : " + listUser.size());

        listViewUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    UserDTO userDTO = (UserDTO) listViewUser.getItemAtPosition(i);
                    Intent intent = new Intent(getActivity(), ProfileEditByAdminActivity.class);
                    intent.putExtra("userID", userDTO.getUserID());
                    startActivity(intent);
                }catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        btnCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateUserActivity.class);
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

}