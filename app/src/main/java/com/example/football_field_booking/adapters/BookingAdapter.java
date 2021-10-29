package com.example.football_field_booking.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.football_field_booking.R;
import com.example.football_field_booking.dtos.BookingDTO;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class BookingAdapter extends BaseAdapter {
    private Context context;
    private List<BookingDTO> listBooking;
    private LayoutInflater layoutInflater;

    public BookingAdapter(Context context) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listBooking = new ArrayList<>();
    }

    public List<BookingDTO> getListBooking() {
        return listBooking;
    }

    public void setListBooking(List<BookingDTO> listBooking) {
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
            rowView = layoutInflater.inflate(R.layout.item_booking, viewGroup, false);
        }

        TextView txtBookingID = rowView.findViewById(R.id.txtBookingID);
        TextView txtTotal = rowView.findViewById(R.id.txtTotal);
        TextView txtBookingDate = rowView.findViewById(R.id.txtBookingDate);

        BookingDTO bookingDTO = listBooking.get(i);

        txtBookingID.setText(bookingDTO.getBookingID());
        txtTotal.setText("$"+bookingDTO.getTotal());
        txtBookingDate.setText(bookingDTO.getBookingDate());

        return rowView;
    }
}
