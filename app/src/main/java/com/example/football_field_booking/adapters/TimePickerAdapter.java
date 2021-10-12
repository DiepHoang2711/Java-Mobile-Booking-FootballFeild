package com.example.football_field_booking.adapters;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.football_field_booking.R;
import com.example.football_field_booking.dtos.TimePickerDTO;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimePickerAdapter extends BaseAdapter {

    private Context context;
    List<TimePickerDTO> timePickerDTOList;
    private LayoutInflater layoutInflater;
    private int startTime, endTime;
    private float price;

    public List<TimePickerDTO> getTimePickerDTOList() {
        return timePickerDTOList;
    }

    public void setTimePickerDTOList(List<TimePickerDTO> timePickerDTOList) {
        this.timePickerDTOList = timePickerDTOList;

    }

    public TimePickerAdapter(Context context) {
        this.context = context;
        this.timePickerDTOList = new ArrayList<>();
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return timePickerDTOList.size();
    }

    @Override
    public Object getItem(int i) {
        return timePickerDTOList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder timePickerHolder;

        timePickerHolder = new ViewHolder();
        view = layoutInflater.inflate(R.layout.item_time_picker, viewGroup, false);

        timePickerHolder.txtStartTime = (TextView) view.findViewById(R.id.txtStartTime);
        timePickerHolder.txtEndTime = (TextView) view.findViewById(R.id.txtEndTime);
        timePickerHolder.edtPrice = (EditText) view.findViewById(R.id.edtPrice);
        timePickerHolder.imgButtonRemoveTP = (ImageButton) view.findViewById(R.id.imgBtnRemoveTP);


        TimePickerDTO timePickerDTO = timePickerDTOList.get(i);

        timePickerHolder.edtPrice.setText(timePickerDTO.getPrice() + "");
        timePickerHolder.txtStartTime.setText(timePickerDTO.getStart() + ":00");
        timePickerHolder.txtEndTime.setText(timePickerDTO.getEnd() + ":00");

        timePickerHolder.imgButtonRemoveTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    timePickerDTOList.remove(i);
                    TimePickerAdapter.this.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.d("Remove error", e.getMessage());
                }

            }
        });

        timePickerHolder.txtStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(android.widget.TimePicker timePicker, int hourOfDay, int minute) {
                                startTime = hourOfDay;
                                timePickerHolder.txtStartTime.setText(startTime + ":00");
                                timePickerDTO.setStart(startTime);
                            }
                        }, 0, 0, true);
                timePickerDialog.updateTime(startTime, 0);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();
            }
        });

        timePickerHolder.txtEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(android.widget.TimePicker timePicker, int hourOfDay, int minute) {
                                endTime = hourOfDay;
                                timePickerHolder.txtEndTime.setText(endTime + ":00");
                                timePickerDTO.setEnd(endTime);
                            }
                        }, startTime + 1, 0, true);
                timePickerDialog.updateTime(endTime, 0);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();
            }
        });


        timePickerHolder.edtPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                price = Float.parseFloat(editable.toString());
                timePickerDTO.setPrice(price);
            }
        });
        return view;
    }

    class ViewHolder {
        TextView txtStartTime;
        TextView txtEndTime;
        EditText edtPrice;
        ImageButton imgButtonRemoveTP;
    }
}
