package com.example.football_field_booking.adapters;

import android.app.TimePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.football_field_booking.R;
import com.example.football_field_booking.dtos.TimePickerDTO;

import java.util.Calendar;
import java.util.List;

public class TimePickerAdapter extends BaseAdapter {

    private Context context;
    List<TimePickerDTO> timePickerDTOList;
    private LayoutInflater layoutInflater;
    private int hour;

    public TimePickerAdapter(Context context, List<TimePickerDTO> timePickerDTOList) {
        this.context = context;
        this.timePickerDTOList = timePickerDTOList;
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
        View timePickerView=view;
        if(timePickerView == null) {
            timePickerView = layoutInflater.inflate(R.layout.item_time_picker, viewGroup, false);
        }
        EditText edtStartTime=timePickerView.findViewById(R.id.edtStartTime);
        EditText edtEndTime=timePickerView.findViewById(R.id.edtEndTime);
        EditText edtPrice=timePickerView.findViewById(R.id.edtPrice);
        ImageButton imgButtonRemoveTP=timePickerView.findViewById(R.id.imgBtnRemoveTP);

        TimePickerDTO timePickerDTO = timePickerDTOList.get(i);
        if(timePickerDTO.getPrice()!=0){
            edtStartTime.setText(timePickerDTO.getStart()+"");
            edtEndTime.setText(timePickerDTO.getEnd()+"");
            edtPrice.setText(timePickerDTO.getPrice()+"");
            imgButtonRemoveTP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    timePickerDTOList.remove(i);
                }
            });
        }else{
            edtStartTime.setText("");
            edtEndTime.setText("");
            edtPrice.setText("");
        }
//        edtStartTime.setTag(i);

        edtStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePickerDialog timePickerDialog=new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(android.widget.TimePicker timePicker, int hourOfDay, int minute) {
                        hour = hourOfDay;
                        Calendar calendar=Calendar.getInstance();
                        calendar.set(0,0,0,hourOfDay,0);
                        edtStartTime.setText(DateFormat.format("hh:mm aa",calendar));
                    }
                },12,0,false);
                timePickerDialog.updateTime(hour, 0);
                timePickerDialog.show();
                Toast.makeText(context, "Success click", Toast.LENGTH_SHORT).show();
            }
        });

        edtEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog=new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(android.widget.TimePicker timePicker, int hourOfDay, int minute) {
                        Calendar calendar=Calendar.getInstance();
                        calendar.set(0,0,0,hourOfDay,minute);
                        edtStartTime.setText(DateFormat.format("hh:mm aa",calendar));
                    }
                },24,0,false);
                timePickerDialog.updateTime(hour, 0);
                timePickerDialog.show();
            }
        });
        return timePickerView;
    }
}
