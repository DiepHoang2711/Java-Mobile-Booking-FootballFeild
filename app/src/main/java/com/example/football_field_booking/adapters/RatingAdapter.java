package com.example.football_field_booking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.football_field_booking.R;
import com.example.football_field_booking.dtos.RatingDTO;

import java.util.ArrayList;
import java.util.List;

public class RatingAdapter extends BaseAdapter {

    private List<RatingDTO> ratingDTOList;
    private Context context;
    private LayoutInflater layoutInflater;

    public RatingAdapter(Context context) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ratingDTOList = new ArrayList<>();
    }

    public List<RatingDTO> getRatingDTOList() {
        return ratingDTOList;
    }

    public void setRatingDTOList(List<RatingDTO> ratingDTOList) {
        this.ratingDTOList = ratingDTOList;
    }

    @Override
    public int getCount() {
        return ratingDTOList.size();
    }

    @Override
    public Object getItem(int i) {
        return ratingDTOList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView = view;
        if(rowView == null) {
            rowView = layoutInflater.inflate(R.layout.item_feedback, viewGroup, false);
        }

        TextView txtFullName = rowView.findViewById(R.id.txtFullName);
        TextView txtRating = rowView.findViewById(R.id.txtRating);
        TextView txtComment = rowView.findViewById(R.id.txtComment);

        RatingDTO dto = ratingDTOList.get(i);

        txtFullName.setText(dto.getUserInfo().getFullName());
        txtRating.setText(dto.getRating()+"");
        txtComment.setText(dto.getComment());

        return rowView;
    }
}
