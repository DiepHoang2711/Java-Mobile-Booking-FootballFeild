package com.example.football_field_booking.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.football_field_booking.R;
import com.example.football_field_booking.adapters.BookingAdapterOfAField;
import com.example.football_field_booking.adapters.RatingAdapter;
import com.example.football_field_booking.daos.FootballFieldDAO;
import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.CartItemDTO;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.example.football_field_booking.dtos.RatingDTO;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListRateAndCommentForOwnerFragment extends Fragment {

    private List<RatingDTO> ratingDTOList;
    private ListView lvRateAndComment;
    private RatingAdapter adapter;

    public ListRateAndCommentForOwnerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_rate_and_comment_for_owner, container, false);
        lvRateAndComment = view.findViewById(R.id.lvRateAndComment);
        Bundle bundle = getArguments();
        String fieldID = bundle.getString("fieldID");
        if (fieldID != null) {
            FootballFieldDAO fieldDAO = new FootballFieldDAO();
            fieldDAO.getRating(fieldID)
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            UserDAO userDAO = new UserDAO();
                            userDAO.getUserById(user.getUid())
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            String role = documentSnapshot.getString("userInfo.role");
                                            ratingDTOList = new ArrayList<>();

                                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                RatingDTO ratingDTO = snapshot.toObject(RatingDTO.class);
                                                ratingDTOList.add(ratingDTO);

                                            }

                                            adapter = new RatingAdapter(getContext());
                                            adapter.setRatingDTOList(ratingDTOList);
                                            adapter.setRole(role);
                                            adapter.setUserID(user.getUid());

                                            lvRateAndComment.setAdapter(adapter);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            e.printStackTrace();
                                        }
                                    });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
        }
        return view;
    }
}