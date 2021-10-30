package com.example.football_field_booking.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.football_field_booking.R;
import com.example.football_field_booking.daos.FootballFieldDAO;
import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.CartItemDTO;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.example.football_field_booking.dtos.RatingDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BookingAdapterOfOwner extends BaseAdapter {

    private Context context;
    private List<CartItemDTO> listBooking;
    private LayoutInflater layoutInflater;
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");

    public BookingAdapterOfOwner(Context context) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listBooking = new ArrayList<>();
    }

    public List<CartItemDTO> getListBooking() {
        return listBooking;
    }

    public void setListBooking(List<CartItemDTO> listBooking) {
        this.listBooking = listBooking;
    }

    @Override
    public int getCount() {
        return listBooking.size();
    }

    @Override
    public Object getItem(int i) {
        return listBooking.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView = view;
        if(rowView == null) {
            rowView = layoutInflater.inflate(R.layout.item_booking_of_owner, viewGroup, false);
        }

        TextView txtFieldName = rowView.findViewById(R.id.txtFieldName);
        TextView txtType = rowView.findViewById(R.id.txtType);
        TextView txtLocation = rowView.findViewById(R.id.txtLocation);
        TextView txtDate = rowView.findViewById(R.id.txtDate);
        TextView txtTotal = rowView.findViewById(R.id.txtTotal);
        ImageView imgField = rowView.findViewById(R.id.imgField);

        TextView txtUserName=rowView.findViewById(R.id.txtUserName);

        CartItemDTO bookingOfAField = listBooking.get(i);
        FootballFieldDTO fieldDTO = bookingOfAField.getFieldInfo();
        txtUserName.setText(bookingOfAField.getUserInfo().getFullName());
        txtFieldName.setText(fieldDTO.getName());
        txtType.setText(fieldDTO.getType());
        txtLocation.setText(fieldDTO.getLocation());
        txtDate.setText(bookingOfAField.getDate());
        txtTotal.setText("$"+bookingOfAField.getTotal());

        if (fieldDTO.getImage() != null) {
            Uri uri = Uri.parse(fieldDTO.getImage());
            Glide.with(imgField.getContext())
                    .load(uri)
                    .into(imgField);
        }

        Calendar calendar = Calendar.getInstance();
        String now = df.format(calendar.getTime());
        return rowView;
    }
}
