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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
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
    private List<Float> priceList;
    List<TimePickerDTO> timePickerDTOList;
    private LayoutInflater layoutInflater;
    private List<TimePickerDTO> chooseList;
    private List<TimePickerDTO> bookedTimeList;

    public List<TimePickerDTO> getTimePickerDTOList() {
        return timePickerDTOList;
    }

    public void setTimePickerDTOList(List<TimePickerDTO> timePickerDTOList) {
        this.timePickerDTOList = timePickerDTOList;
    }

    public List<TimePickerDTO> getChooseList() {
        return chooseList;
    }

    public void setChooseList(List<TimePickerDTO> chooseList) {
        this.chooseList = chooseList;
    }

    public TimePickerDetailAdapter(Context context, List<TimePickerDTO> list, List<TimePickerDTO> chooseList, List<TimePickerDTO> bookedTimeList) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.priceList = new ArrayList<>();
        this.chooseList = chooseList;
        this.bookedTimeList = bookedTimeList;
        timePickerDTOList = new ArrayList<>();
        try {
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
            Log.d("USER", "listPrice: " + priceList.toString());

            for (TimePickerDTO dto : list) {
                for (int i = dto.getStart(); i < dto.getEnd(); i++) {
                    TimePickerDTO timePickerDTO = new TimePickerDTO();
                    timePickerDTO.setStart(i);
                    timePickerDTO.setEnd(i + 1);
                    timePickerDTO.setPrice(dto.getPrice());
                    timePickerDTOList.add(timePickerDTO);
                }
            }
            Log.d("USER", "listDto: " + timePickerDTOList.toString());
        }catch (Exception e) {
            e.printStackTrace();
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
        try {
            TextView txtPrice = view.findViewById(R.id.txtPrice);
            GridLayout groupToggleButton = view.findViewById(R.id.groupToggleButton);
            groupToggleButton.removeAllViewsInLayout();
            Float price = priceList.get(i);
            txtPrice.setText("$"+price+"/h");
            Log.d("USER", i + "  listDTO: " + timePickerDTOList.toString());
            for (TimePickerDTO dto : timePickerDTOList) {
                if (dto.getPrice() == price) {
                    ToggleButton toggleButton = new ToggleButton(view.getContext());
                    toggleButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    toggleButton.setBackground(view.getResources().getDrawable(R.drawable.toggle_selector));
                    toggleButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    toggleButton.setTextSize(12);
                    toggleButton.setText(dto.getStart() + "-" + dto.getEnd());
                    toggleButton.setTextOn(dto.getStart() + "-" + dto.getEnd());
                    toggleButton.setTextOff(dto.getStart() + "-" + dto.getEnd());

                    for (TimePickerDTO chooseDTO : chooseList) {
                        if (dto.getStart() == chooseDTO.getStart() && dto.getEnd() == chooseDTO.getEnd()) {
                            toggleButton.setChecked(true);
                        }
                    }

                    for (TimePickerDTO bookedDTO : bookedTimeList) {
                        if (dto.getStart() == bookedDTO.getStart() && dto.getEnd() == bookedDTO.getEnd()) {
                            toggleButton.setEnabled(false);
                        }
                    }

                    groupToggleButton.addView(toggleButton);
                    toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b) {
                                chooseList.add(dto);
                            } else {
                                chooseList.remove(dto);
                            }
                        }
                    });
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

}

