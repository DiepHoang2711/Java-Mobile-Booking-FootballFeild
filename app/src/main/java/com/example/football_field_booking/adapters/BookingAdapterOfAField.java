package com.example.football_field_booking.adapters;

import android.content.Context;
import android.util.Log;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BookingAdapterOfAField extends BaseAdapter {

    private Context context;
    private List<CartItemDTO> listBooking;
    private LayoutInflater layoutInflater;
    private List<String> timeBookingAtList;
    private static final SimpleDateFormat dfFull = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


    public void setTimeBookingAtList(List<String> timeBookingAtList) {
        this.timeBookingAtList = timeBookingAtList;
    }

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
        if (rowView == null) {
            rowView = layoutInflater.inflate(R.layout.item_booking_of_a_field, viewGroup, false);
        }
        TextView txtDate = rowView.findViewById(R.id.txtDate);
        TextView txtTotal = rowView.findViewById(R.id.txtTotal);
        TextView txtUserName = rowView.findViewById(R.id.txtUserName);
        TextView txtBookingAt = rowView.findViewById(R.id.txtBookingAt);
        GridLayout gridLayout = rowView.findViewById(R.id.gridLayoutTimePicker);
        gridLayout.removeAllViewsInLayout();

        CartItemDTO bookingOfAField = listBooking.get(i);
        txtUserName.setText(bookingOfAField.getUserInfo().getFullName());
        txtDate.setText(bookingOfAField.getDate());
        txtTotal.setText("$" + bookingOfAField.getTotal());

        Calendar calendarNow = Calendar.getInstance();
        String bookingAt = timeBookingAtList.get(i);

        Calendar calendarBookingAt = Calendar.getInstance();
        try {
            calendarBookingAt.setTime(dfFull.parse(bookingAt));

            int yearOfNow = calendarNow.get(Calendar.YEAR);
            int yearOfBookingAt = calendarBookingAt.get(Calendar.YEAR);
            int monthOfNow = calendarNow.get(Calendar.MONTH);
            int monthOfBookingAt = calendarBookingAt.get(Calendar.MONTH);
            int dayOfNow = calendarNow.get(Calendar.DAY_OF_MONTH);
            int dayOfBookingAt = calendarBookingAt.get(Calendar.DAY_OF_MONTH);
            int hourOfNow = calendarNow.get(Calendar.HOUR_OF_DAY);
            int hourOfBookingAt = calendarBookingAt.get(Calendar.HOUR_OF_DAY);
            int minuteOfNow = calendarNow.get(Calendar.MINUTE);
            int minuteOfBookingAt = calendarBookingAt.get(Calendar.MINUTE);
            int secondOfNow = calendarNow.get(Calendar.SECOND);
            int secondOfBookingAt = calendarBookingAt.get(Calendar.SECOND);

            int minuteAgo=0;
            if (yearOfNow == yearOfBookingAt) {
                if (monthOfNow == monthOfBookingAt) {
                    if (dayOfNow == dayOfBookingAt) {
                        if (hourOfNow == hourOfBookingAt) {
                            if (minuteOfNow == minuteOfBookingAt) {
                                bookingAt = secondOfNow - secondOfBookingAt + " seconds ago";
                            } else {
                                minuteAgo=minuteOfNow - minuteOfBookingAt;
                                if(minuteAgo==1){
                                    bookingAt =minuteAgo + " minute ago";
                                }else{
                                    bookingAt =minuteAgo + " minutes ago";
                                }
                            }
                        } else {
                            int hoursAgo = ((hourOfNow * 60 + minuteOfNow)-(hourOfBookingAt * 60 + minuteOfBookingAt))/60;
                            if (hoursAgo >= 1) {
                                if(hoursAgo==1){
                                    bookingAt = hoursAgo + " hour ago";
                                }else{
                                    bookingAt = hoursAgo + " hours ago";
                                }

                            } else {
                                minuteAgo=(hourOfNow * 60 + minuteOfNow) - (hourOfBookingAt * 60 + minuteOfBookingAt);
                                if(minuteAgo==1){
                                    bookingAt = minuteAgo + " minute ago";
                                }else{
                                    bookingAt = minuteAgo + " minutes ago";
                                }

                            }
                        }
                    }
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        txtBookingAt.setText(bookingAt);

        for (TimePickerDTO dto : bookingOfAField.getTimePicker()) {
            TextView txtTime = new TextView(context);
            TextView txtPrice = new TextView(context);
            txtTime.setText(dto.getStart() + "h " + "- " + dto.getEnd() + "h");
            txtPrice.setText("               $" + dto.getPrice());
            gridLayout.addView(txtTime);
            gridLayout.addView(txtPrice);
        }

        return rowView;
    }
}
