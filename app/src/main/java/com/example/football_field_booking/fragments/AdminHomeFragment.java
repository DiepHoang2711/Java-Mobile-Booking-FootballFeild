package com.example.football_field_booking.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.football_field_booking.CreateFootballFieldActivity;
import com.example.football_field_booking.CreateUserActivity;
import com.example.football_field_booking.EditProfileByAdminActivity;
import com.example.football_field_booking.R;
import com.example.football_field_booking.SearchUserActivity;
import com.example.football_field_booking.adapters.UserAdapter;
import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.UserDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeFragment extends Fragment {

    private ListView listViewUser;
    private FloatingActionButton btnCreateUser;
    private AutoCompleteTextView auComTxtRole;
    private ImageButton btnSearchByRole;

    public AdminHomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        listViewUser = view.findViewById(R.id.listViewUser);
        btnCreateUser = view.findViewById(R.id.btnCreateUser);
        auComTxtRole = view.findViewById(R.id.auComTxtRole);
        btnSearchByRole = view.findViewById(R.id.btnSearchByRole);

        listViewUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    UserDTO userDTO = (UserDTO) listViewUser.getItemAtPosition(i);
                    Intent intent = new Intent(getActivity(), EditProfileByAdminActivity.class);
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

        btnSearchByRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String role = auComTxtRole.getText().toString();
                Intent intent = new Intent(getActivity(), SearchUserActivity.class);
                intent.putExtra("role", role);
                startActivity(intent);
            }
        });

        loadDataRoles();

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        loadData();

    }

    private void loadData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserDAO userDAO = new UserDAO();
        List<UserDTO> listUser = new ArrayList<>();
        userDAO.getAllUser().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                    UserDTO dto = doc.get("userInfo", UserDTO.class);
                    if(!user.getUid().equals(dto.getUserID())){
                        listUser.add(dto);
                    }
                }
                UserAdapter adapter = new UserAdapter(getActivity(),listUser );
                listViewUser.setAdapter(adapter);
                Log.d("USER", "LIST : " + listUser.toString());
                Log.d("USER", "adapter : " + adapter.getCount());
            }
        });
        Log.d("USER", "Size : " + listUser.size());
    }

    private void loadDataRoles() {
        UserDAO userDAO = new UserDAO();
        userDAO.getConstOfUser().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> listRole = (ArrayList<String>) documentSnapshot.get("roles");
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, listRole);
                auComTxtRole.setAdapter(adapter);
                if(!listRole.isEmpty()){
                    auComTxtRole.setText(listRole.get(0), false);
                }

            }
        });
    }
}