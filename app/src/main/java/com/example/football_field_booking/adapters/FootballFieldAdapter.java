package com.example.football_field_booking.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.football_field_booking.R;
import com.example.football_field_booking.dtos.FootballFieldDTO;

import java.util.List;

public class FootballFieldAdapter extends BaseAdapter {

    private Context context;
    private List<FootballFieldDTO> fieldDTOList;
    private LayoutInflater layoutInflater;
    private List<Double> distanceList;

    public FootballFieldAdapter(Context context, List<FootballFieldDTO> fieldDTOList) {
        this.context = context;
        this.fieldDTOList = fieldDTOList;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public List<Double> getDistanceList() {
        return distanceList;
    }

    public void setDistanceList(List<Double> distanceList) {
        this.distanceList = distanceList;
    }

    @Override
    public int getCount() {
        return fieldDTOList.size();
    }

    @Override
    public Object getItem(int i) {
        return fieldDTOList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View rowView = view;
        if(rowView == null) {
            rowView = layoutInflater.inflate(R.layout.item_football_field, viewGroup, false);
        }
        ImageView imgField = rowView.findViewById(R.id.imgFootBallField);
        TextView txtFieldName = rowView.findViewById(R.id.txtFieldName);
        TextView txtLocation = rowView.findViewById(R.id.txtLocation);
        TextView txtType=rowView.findViewById(R.id.txtType);
        TextView txtRate=rowView.findViewById(R.id.txtRate);
        LinearLayout lnItemField=rowView.findViewById(R.id.lnItemField);
        FootballFieldDTO dto = fieldDTOList.get(i);

        if (dto.getImage() != null) {
            Uri uri = Uri.parse(dto.getImage());
            Glide.with(imgField.getContext())
                    .load(uri)
                    .into(imgField);
        }

        txtFieldName.setText(dto.getName());
        txtLocation.setText(dto.getLocation());
        txtType.setText(dto.getType());
        float rate = Math.round(dto.getRate() * 10f) / 10f;
        txtRate.setText(rate+"");

        if(dto.getStatus().equals("inactive")){
            lnItemField.setAlpha(0.3F);
        }else{
            lnItemField.setAlpha(1);
        }

        if(distanceList != null) {
            CardView cardDistance = rowView.findViewById(R.id.cardDistance);
            cardDistance.setVisibility(View.VISIBLE);
            TextView txtDistance = rowView.findViewById(R.id.txtDistance);
            txtDistance.setText(distanceList.get(i)+ " km");
        }

        return rowView;
    }
}
