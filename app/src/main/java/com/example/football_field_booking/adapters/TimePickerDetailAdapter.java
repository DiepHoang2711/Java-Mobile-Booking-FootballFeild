package com.example.football_field_booking.adapters;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.football_field_booking.R;
import com.example.football_field_booking.dtos.TimePickerDTO;
import com.example.football_field_booking.utils.Utils;
import com.example.football_field_booking.validations.Validation;
import com.google.android.material.textfield.TextInputLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimePickerDetailAdapter extends BaseAdapter {

    private Context context;
    List<TimePickerDTO> timePickerDTOList;
    private LayoutInflater layoutInflater;
    private List<Float> priceList;
    private List<TimePickerDTO> chooseList;

    public List<TimePickerDTO> getTimePickerDTOList() {
        return timePickerDTOList;
    }


    public TimePickerDetailAdapter(Context context, List<TimePickerDTO> list) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        priceList = new ArrayList<>();
        chooseList=new ArrayList<>();
        for (TimePickerDTO timePickerDTO : list) {
            boolean isAdd = true;
            for (Float price : priceList) {
                if (price == timePickerDTO.getPrice()) {
                    isAdd = false;
                }
            }
            if (isAdd) {
                priceList.add(timePickerDTO.getPrice());
            }
        }
        timePickerDTOList=new ArrayList<>();

        for(TimePickerDTO dto:list){
            for(int i= dto.getStart();i<dto.getEnd();i++){
                TimePickerDTO timePickerDTO=new TimePickerDTO();
                timePickerDTO.setStart(i);
                timePickerDTO.setEnd(i+1);
                timePickerDTO.setPrice(dto.getPrice());
                timePickerDTOList.add(timePickerDTO);
            }
        }

    }


    @Override
    public int getCount() {
        return priceList.size();
    }

    @Override
    public Object getItem(int i) {
        return priceList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView = view;
        if (rowView == null) {
            view = layoutInflater.inflate(R.layout.item_time_picker_detail, viewGroup, false);
        }
        TextView txtPrice = view.findViewById(R.id.txtPrice);
        GridLayout groupToggleButton = view.findViewById(R.id.groupToggleButton);
        groupToggleButton.removeAllViewsInLayout();
        Float price=priceList.get(i);
        txtPrice.setText("$"+price+"/h");
        for (TimePickerDTO dto : timePickerDTOList) {
            if(dto.getPrice()==price){
                ToggleButton toggleButton = new ToggleButton(context);
                toggleButton.setTextAppearance(context,R.style.MyToggleButton);
                toggleButton.setBackground(context.getResources().getDrawable(R.drawable.toggle_selector));
                LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(200,100);
                params.setMargins(10,5,10,5);
                toggleButton.setLayoutParams(params);
                toggleButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                toggleButton.setText(dto.getStart()+"h-"+dto.getEnd()+"h");
                groupToggleButton.addView(toggleButton);
                toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b){
                            toggleButton.setTextOn(dto.getStart()+"h-"+dto.getEnd()+"h");
                            chooseList.add(dto);
                        }else{
                            toggleButton.setTextOff(dto.getStart()+"h-"+dto.getEnd()+"h");
                            chooseList.remove(dto);
                        }
                    }
                });
            }
        }
        Log.d("TimePickerDetailAdapter",timePickerDTOList.size()+"");
        return view;
    }
}

