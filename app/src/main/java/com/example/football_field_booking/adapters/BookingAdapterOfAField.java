package com.example.football_field_booking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.TextView;

import com.example.football_field_booking.R;
import com.example.football_field_booking.dtos.CartItemDTO;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.example.football_field_booking.dtos.TimePickerDTO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BookingAdapterOfAField extends BaseAdapter {

    private Context context;
    private List<CartItemDTO> listBooking;
    private LayoutInflater layoutInflater;

    public BookingAdapterOfAField(Context context) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listBooking = new ArrayList<>();
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
            rowView = layoutInflater.inflate(R.layout.item_booking_of_a_field, viewGroup, false);
        }
        TextView txtDate = rowView.findViewById(R.id.txtDate);
        TextView txtTotal = rowView.findViewById(R.id.txtTotal);
        TextView txtUserName=rowView.findViewById(R.id.txtUserName);
        GridLayout gridLayout = rowView.findViewById(R.id.gridLayoutTimePicker);
        gridLayout.removeAllViewsInLayout();

        CartItemDTO bookingOfAField = listBooking.get(i);
        txtUserName.setText(bookingOfAField.getUserInfo().getFullName());
        txtDate.setText(bookingOfAField.getDate());
        txtTotal.setText("$"+bookingOfAField.getTotal());


        for (TimePickerDTO dto: bookingOfAField.getTimePicker()) {
            TextView txtTime = new TextView(context);
            TextView txtPrice = new TextView(context);
            txtTime.setText(dto.getStart()+"h "+"- " + dto.getEnd()+ "h");
            txtPrice.setText("               $"+dto.getPrice());
            gridLayout.addView(txtTime);
            gridLayout.addView(txtPrice);
        }

        return rowView;
    }
}
