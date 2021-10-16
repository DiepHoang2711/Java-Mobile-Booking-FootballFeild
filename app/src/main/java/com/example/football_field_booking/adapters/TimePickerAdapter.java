package com.example.football_field_booking.adapters;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
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

public class TimePickerAdapter extends BaseAdapter {

    private Context context;
    List<TimePickerDTO> timePickerDTOList;
    private LayoutInflater layoutInflater;
    private int startTime, endTime;
    private float price;
    private List<String> listError;

    public List<TimePickerDTO> getTimePickerDTOList() {
        return timePickerDTOList;
    }

    public void setTimePickerDTOList(List<TimePickerDTO> timePickerDTOList) {
        this.timePickerDTOList = timePickerDTOList;
    }

    public List<String> getListError() {
        return listError;
    }

    public void setListError(List<String> listError) {
        this.listError = listError;
    }

    public TimePickerAdapter(Context context) {
        this.context = context;
        this.timePickerDTOList = new ArrayList<>();
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listError = new ArrayList<>();
    }

    public TimePickerAdapter(Context context, List<TimePickerDTO> list) {
        this.context = context;
        this.timePickerDTOList = list;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listError = new ArrayList<>();
        for (TimePickerDTO dto: list) {
            listError.add("");
        }
    }

    public void addNewTimePickerWorking() {
        TimePickerDTO timePickerDTO = new TimePickerDTO();
        timePickerDTO.setStart(-1);
        timePickerDTO.setEnd(-1);
        timePickerDTO.setPrice(-1);
        getTimePickerDTOList().add(timePickerDTO);
        listError.add("Input your working time");
        TimePickerAdapter.this.notifyDataSetChanged();
    }

    private void checkValidTimePicker (int position) {
        try {
            listError.set(position, "");
            TimePickerDTO dto = timePickerDTOList.get(position);
            Log.d("USER", "dto: " + dto.toString());
            if (dto.getStart() < 0) {
                listError.set(position, "Start time require!");
                return;
            }
            if (dto.getEnd() < 0) {
                listError.set(position, "End time require!");
                return;
            }
            if (dto.getPrice() < 0) {
                listError.set(position, "Price require!");
                return;
            }
            if (dto.getEnd() <= dto.getStart()){
                listError.set(position, "End time must better than start time!");
                return;
            }

            for (int i = 0; i < timePickerDTOList.size(); i++) {
                if(position != i){
                    if(timePickerDTOList.get(i).getStart() < dto.getEnd()) {
                        if(timePickerDTOList.get(i).getEnd() > dto.getStart()) {
                            listError.set(position, "Time conflict!");
                            return;
                        }
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isValidTimePicker () {
        boolean result = true;
        for (String error: listError ) {
            if(!error.equals("")) {
                result = false;
                break;
            }
        }
        return result;
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
        timePickerHolder.tlPrice = (TextInputLayout) view.findViewById(R.id.tlPrice);
        timePickerHolder.imgButtonRemoveTP = (ImageButton) view.findViewById(R.id.imgBtnRemoveTP);
        timePickerHolder.txtError = (TextView) view.findViewById(R.id.txtError);

        TimePickerDTO timePickerDTO = timePickerDTOList.get(i);

        if(timePickerDTO.getStart()==-1){
            timePickerHolder.txtStartTime.setText("Start");
        }else{
            timePickerHolder.txtStartTime.setText(timePickerDTO.getStart() + ":00");
        }

        if(timePickerDTO.getEnd()==-1){
            timePickerHolder.txtEndTime.setText("End");
        }else{
            timePickerHolder.txtEndTime.setText(timePickerDTO.getEnd() + ":00");
        }

        if(timePickerDTO.getPrice()>-1){
            timePickerHolder.tlPrice.getEditText().setText(timePickerDTO.getPrice() + "");
        }
        timePickerHolder.txtError.setText(listError.get(i));

        timePickerHolder.imgButtonRemoveTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    timePickerDTOList.remove(i);
                    listError.remove(i);
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
                                checkValidTimePicker(i);
                                TimePickerAdapter.this.notifyDataSetChanged();
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
                                checkValidTimePicker(i);
                                TimePickerAdapter.this.notifyDataSetChanged();
                            }
                        }, startTime + 1, 0, true);
                timePickerDialog.updateTime(endTime, 0);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();
            }
        });

        timePickerHolder.tlPrice.getEditText().setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event != null &&
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (event == null || !event.isShiftPressed()) {
                                // the user is done typing.
                                try {
                                    price = Float.parseFloat(v.getText().toString());
                                    timePickerDTO.setPrice(price);
                                }catch (Exception e) {
                                    timePickerDTO.setPrice(-1);
                                }
                                checkValidTimePicker(i);
                                TimePickerAdapter.this.notifyDataSetChanged();
                                return true; // consume.
                            }
                        }
                        return false; // pass on to other listeners.
                    }
                }
        );
        return view;
    }

    class ViewHolder {
        TextView txtStartTime;
        TextView txtEndTime;
        TextInputLayout tlPrice;
        ImageButton imgButtonRemoveTP;
        TextView txtError;
    }
}
