package com.example.football_field_booking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.football_field_booking.R;
import com.example.football_field_booking.dtos.UserDTO;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class userAdapter extends BaseAdapter {

    private Context context;
    private List<UserDTO> listUser;
    private LayoutInflater layoutInflater;
    public static final String USER_FOLDER_IMAGES = "user_images";
    private FirebaseStorage storage;

    public userAdapter(Context context, List<UserDTO> listUser) {
        this.context = context;
        this.listUser = listUser;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return listUser.size();
    }

    @Override
    public Object getItem(int i) {
        return listUser.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View  view, ViewGroup viewGroup) {
        View rowView = view;
        if(rowView == null) {
            rowView = layoutInflater.inflate(R.layout.item_user, null, true);
        }
        ImageView imgUser = rowView.findViewById(R.id.imgUser);
        TextView txtFullName = rowView.findViewById(R.id.txtFullName);
        TextView txtUserID = rowView.findViewById(R.id.txtUserID);

        UserDTO user = listUser.get(i);

        if (user.getPhoto() != null) {
            StorageReference storageRef = storage.getReference().child(USER_FOLDER_IMAGES + "/" + user.getPhoto());
            Glide.with(imgUser.getContext())
                    .load(storageRef)
                    .into(imgUser);
        }
        txtFullName.setText(user.getFullName());
        txtUserID.setText(user.getUserID());

        return rowView;
    }
}
