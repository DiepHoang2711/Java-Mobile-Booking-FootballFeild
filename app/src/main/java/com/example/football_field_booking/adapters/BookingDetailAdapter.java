package com.example.football_field_booking.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.football_field_booking.R;
import com.example.football_field_booking.daos.FootballFieldDAO;
import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.BookingDetailDTO;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.example.football_field_booking.dtos.FootballFieldDocument;
import com.example.football_field_booking.dtos.RatingDTO;
import com.example.football_field_booking.dtos.TimePickerDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BookingDetailAdapter extends BaseAdapter {

    private Context context;
    private List<BookingDetailDTO> booking;
    private LayoutInflater layoutInflater;
    private String bookingID;
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");

    public BookingDetailAdapter(Context context) {
        this.context = context;
        booking = new ArrayList<>();
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public String getBookingID() {
        return bookingID;
    }

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }

    public List<BookingDetailDTO> getBooking() {
        return booking;
    }

    public void setBooking(List<BookingDetailDTO> booking) {
        this.booking = booking;
    }

    @Override
    public int getCount() {
        return booking.size();
    }

    @Override
    public Object getItem(int i) {
        return booking.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView = view;
        if(rowView == null) {
            rowView = layoutInflater.inflate(R.layout.item_booking_detail, viewGroup, false);
        }

        TextView txtFieldName = rowView.findViewById(R.id.txtFieldName);
        TextView txtType = rowView.findViewById(R.id.txtType);
        TextView txtLocation = rowView.findViewById(R.id.txtLocation);
        TextView txtDate = rowView.findViewById(R.id.txtDate);
        TextView txtTotal = rowView.findViewById(R.id.txtTotal);
        ImageView imgField = rowView.findViewById(R.id.imgField);
        GridLayout gridLayout = rowView.findViewById(R.id.gridLayoutTimePicker);
        gridLayout.removeAllViewsInLayout();

        BookingDetailDTO bookingDetailDTO = booking.get(i);
        FootballFieldDTO fieldDTO = bookingDetailDTO.getFieldInfo();
        List<TimePickerDTO> timePickerDTOList = bookingDetailDTO.getTimePicker();

        txtFieldName.setText(fieldDTO.getName());
        txtType.setText(fieldDTO.getType());
        txtLocation.setText(fieldDTO.getLocation());
        txtDate.setText(bookingDetailDTO.getDate());
        txtTotal.setText("$"+bookingDetailDTO.getTotal());


        if (fieldDTO.getImage() != null) {
            Uri uri = Uri.parse(fieldDTO.getImage());
            Glide.with(imgField.getContext())
                    .load(uri)
                    .into(imgField);
        }

        for (TimePickerDTO dto: timePickerDTOList) {
            TextView txtTime = new TextView(context);
            TextView txtPrice = new TextView(context);
            txtTime.setText(dto.getStart()+"h "+"- " + dto.getEnd()+ "h");
            txtPrice.setText("$"+dto.getPrice());
            gridLayout.addView(txtTime);
            gridLayout.addView(txtPrice);
        }

        Calendar calendar = Calendar.getInstance();
        String now = df.format(calendar.getTime());
        try {
            if(now.compareTo(bookingDetailDTO.getDate()) >= 1 && !bookingDetailDTO.isAlreadyRating()) {
                LinearLayout layoutFeedback = rowView.findViewById(R.id.layoutFeedback);
                layoutFeedback.setVisibility(View.VISIBLE);
                RatingBar rbRating = rowView.findViewById(R.id.rbRating);
                Button btnSubmit = rowView.findViewById(R.id.btnSubmit);

                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        float rating = rbRating.getRating();
                        Calendar calendar = Calendar.getInstance();
                        String now = df.format(calendar.getTime());
                        RatingDTO ratingDTO = new RatingDTO(bookingDetailDTO.getUserInfo(), fieldDTO, rating, now);
                        UserDAO userDAO = new UserDAO();
                        Log.d("USER", "onClickSubmit: ");
                        userDAO.rating(ratingDTO, bookingID, bookingDetailDTO.getID()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    FootballFieldDAO fieldDAO = new FootballFieldDAO();
                                    fieldDAO.getFieldByID(fieldDTO.getFieldID()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            try {
                                                FootballFieldDocument document = documentSnapshot.toObject(FootballFieldDocument.class);
                                                fieldDAO.countRating(document.getFieldInfo(), document.getOwnerInfo().getUserID());
                                                booking.get(i).setAlreadyRating(true);
                                                layoutFeedback.setVisibility(View.GONE);
                                                Toast.makeText(context, "Rating success", Toast.LENGTH_SHORT).show();
                                            }catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                }else {
                                    task.getException().printStackTrace();
                                }
                            }
                        });

                    }
                });

            }
        }catch (Exception e) {
            e.printStackTrace();
        }


        return rowView;
    }
}
