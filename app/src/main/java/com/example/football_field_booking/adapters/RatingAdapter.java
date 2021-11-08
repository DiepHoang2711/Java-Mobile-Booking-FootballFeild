package com.example.football_field_booking.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.football_field_booking.R;
import com.example.football_field_booking.daos.FootballFieldDAO;
import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.example.football_field_booking.dtos.FootballFieldDocument;
import com.example.football_field_booking.dtos.RatingDTO;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RatingAdapter extends BaseAdapter {

    private List<RatingDTO> ratingDTOList;
    private Context context;
    private LayoutInflater layoutInflater;
    private String role;
    private String userID;

    public void setUserID(String userID) {
        this.userID = userID;
    }

    private static final SimpleDateFormat dfFull = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public void setRole(String role) {
        this.role = role;
    }

    public RatingAdapter(Context context) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ratingDTOList = new ArrayList<>();
    }

    public void setRatingDTOList(List<RatingDTO> ratingDTOList) {
        this.ratingDTOList = ratingDTOList;
    }

    @Override
    public int getCount() {
        return ratingDTOList.size();
    }

    @Override
    public Object getItem(int i) {
        return ratingDTOList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView = view;
        if(rowView == null) {
            rowView = layoutInflater.inflate(R.layout.item_feedback, viewGroup, false);
        }

        TextView txtFullName = rowView.findViewById(R.id.txtFullName);
        TextView txtRating = rowView.findViewById(R.id.txtRating);
        TextView txtComment = rowView.findViewById(R.id.txtComment);
        ImageView imgAvatar=rowView.findViewById(R.id.imgAvatar);
        TextView txtDate=rowView.findViewById(R.id.txtDate);
        RatingDTO dto = ratingDTOList.get(i);
        Button txtDelete=rowView.findViewById(R.id.txtDelete);

        Uri uri = Uri.parse(dto.getUserInfo().getPhotoUri());
        Glide.with(imgAvatar.getContext())
                .load(uri)
                .into(imgAvatar);

        if(role!=null && role.equals("owner")){
            txtDelete.setVisibility(View.VISIBLE);
            txtDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserDAO userDAO=new UserDAO();
                    userDAO.deleteRating(dto).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            FootballFieldDAO fieldDAO=new FootballFieldDAO();
                            try {
                                fieldDAO.getFieldByID(dto.getFieldInfo().getFieldID())
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                FootballFieldDocument document = documentSnapshot.toObject(FootballFieldDocument.class);
                                                try {
                                                    fieldDAO.countRating(document.getFieldInfo(),document.getOwnerInfo().getUserID());
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                ratingDTOList.remove(dto);
                                                RatingAdapter.this.notifyDataSetChanged();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            });
        }else{
            txtDelete.setVisibility(View.GONE);
        }
        txtFullName.setText(dto.getUserInfo().getFullName());
        txtRating.setText(dto.getRating()+"");
        txtComment.setText(dto.getComment());
        Calendar calendarNow = Calendar.getInstance();
        String dateRateAndComment = dto.getDate();

        Calendar calendarRateAndComment = Calendar.getInstance();
        try {
            calendarRateAndComment.setTime(dfFull.parse(dateRateAndComment));

            int yearOfNow = calendarNow.get(Calendar.YEAR);
            int yearOfRateAndComment = calendarRateAndComment.get(Calendar.YEAR);
            int monthOfNow = calendarNow.get(Calendar.MONTH);
            int monthOfRateAndComment = calendarRateAndComment.get(Calendar.MONTH);
            int dayOfNow = calendarNow.get(Calendar.DAY_OF_MONTH);
            int dayOfRateAndComment = calendarRateAndComment.get(Calendar.DAY_OF_MONTH);
            int hourOfNow = calendarNow.get(Calendar.HOUR_OF_DAY);
            int hourOfRateAndComment = calendarRateAndComment.get(Calendar.HOUR_OF_DAY);
            int minuteOfNow = calendarNow.get(Calendar.MINUTE);
            int minuteOfRateAndComment = calendarRateAndComment.get(Calendar.MINUTE);
            int secondOfNow = calendarNow.get(Calendar.SECOND);
            int secondOfRateAndComment = calendarRateAndComment.get(Calendar.SECOND);

            int minuteAgo=0;
            if (yearOfNow == yearOfRateAndComment) {
                if (monthOfNow == monthOfRateAndComment) {
                    if (dayOfNow == dayOfRateAndComment) {
                        if (hourOfNow == hourOfRateAndComment) {
                            if (minuteOfNow == minuteOfRateAndComment) {
                                dateRateAndComment = secondOfNow - secondOfRateAndComment + " seconds ago";
                            } else {
                                minuteAgo=minuteOfNow - minuteOfRateAndComment;
                                if(minuteAgo==1){
                                    dateRateAndComment =minuteAgo + " minute ago";
                                }else{
                                    dateRateAndComment =minuteAgo + " minutes ago";
                                }
                            }
                        } else {
                            int hoursAgo = ((hourOfNow * 60 + minuteOfNow)-(hourOfRateAndComment * 60 + minuteOfRateAndComment))/60;
                            if (hoursAgo >= 1) {
                                if(hoursAgo==1){
                                    dateRateAndComment = hoursAgo + " hour ago";
                                }else{
                                    dateRateAndComment = hoursAgo + " hours ago";
                                }

                            } else {
                                minuteAgo=(hourOfNow * 60 + minuteOfNow) - (hourOfRateAndComment * 60 + minuteOfRateAndComment);
                                if(minuteAgo==1){
                                    dateRateAndComment = minuteAgo + " minute ago";
                                }else{
                                    dateRateAndComment = minuteAgo + " minutes ago";
                                }

                            }
                        }
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        txtDate.setText(dateRateAndComment);

        return rowView;
    }
}
