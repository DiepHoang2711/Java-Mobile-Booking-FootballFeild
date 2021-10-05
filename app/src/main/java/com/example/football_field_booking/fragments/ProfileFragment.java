package com.example.football_field_booking.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.football_field_booking.MainActivity;
import com.example.football_field_booking.ProfileEditActivity;
import com.example.football_field_booking.R;
import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.UserDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileFragment extends Fragment {

    private Button btnLogout;
    private Button btnUpdateUser;
    private TextView txtFullName;
    private ImageView imgUser;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        txtFullName = view.findViewById(R.id.txtFullName);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnUpdateUser = view.findViewById(R.id.btnUpdateUser);
        imgUser = view.findViewById(R.id.imgUser);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserDAO userDAO = new UserDAO();
        userDAO.getUserById(user.getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    UserDTO userDTO = task.getResult().toObject(UserDTO.class);
                    txtFullName.setText(userDTO.getFullName());
                    if (userDTO.getPhotoUri() != null) {
                        Uri uri = Uri.parse(userDTO.getPhotoUri());
                        Glide.with(imgUser.getContext())
                                .load(uri)
                                .into(imgUser);
                    } else {
                        imgUser.setImageResource(R.drawable.outline_account_circle_24);
                    }
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences userSPREF = getActivity().getSharedPreferences(
                        getResources().getString(R.string.user_share_pref), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = userSPREF.edit();
                editor.remove("role").apply();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        btnUpdateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
                startActivity(intent);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
}