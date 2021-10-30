package com.example.football_field_booking.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.football_field_booking.R;
import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.CartItemDTO;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.example.football_field_booking.dtos.TimePickerDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends BaseAdapter {

    private Context context;
    private List<CartItemDTO> cart;
    private LayoutInflater layoutInflater;

    public CartAdapter(Context context) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.cart = new ArrayList<>();
    }

    public List<CartItemDTO> getCart() {
        return cart;
    }

    public void setCart(List<CartItemDTO> cart) {
        this.cart = cart;
    }

    @Override
    public int getCount() {
        return cart.size();
    }

    @Override
    public Object getItem(int i) {
        return cart.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView = view;
        if (rowView == null) {
            rowView = layoutInflater.inflate(R.layout.item_cart, viewGroup, false);
        }
        TextView txtFieldName = rowView.findViewById(R.id.txtFieldName);
        TextView txtType = rowView.findViewById(R.id.txtType);
        TextView txtLocation = rowView.findViewById(R.id.txtLocation);
        TextView txtDate = rowView.findViewById(R.id.txtDate);
        TextView txtTotal = rowView.findViewById(R.id.txtTotal);
        ImageView imgField = rowView.findViewById(R.id.imgField);
        ImageButton btnRemove = rowView.findViewById(R.id.btnRemove);
        GridLayout gridLayout = rowView.findViewById(R.id.gridLayoutTimePicker);
        gridLayout.removeAllViewsInLayout();

        CartItemDTO cartItemDTO = cart.get(i);
        FootballFieldDTO fieldDTO = cartItemDTO.getFieldInfo();
        List<TimePickerDTO> timePickerDTOList = cartItemDTO.getTimePicker();

        Log.d("USER", "i: " + i);

        txtFieldName.setText(fieldDTO.getName());
        txtType.setText(fieldDTO.getType());
        txtLocation.setText(fieldDTO.getLocation());
        txtDate.setText(cartItemDTO.getDate());
        txtTotal.setText("$"+cartItemDTO.getTotal());

        if (fieldDTO.getImage() != null) {
            Uri uri = Uri.parse(fieldDTO.getImage());
            Glide.with(imgField.getContext())
                    .load(uri)
                    .into(imgField);
        }

        for (TimePickerDTO dto: timePickerDTOList) {
            TextView txtTime = new TextView(context);
            TextView txtPrice = new TextView(context);
            txtTime.setText(dto.getStart()+"h "+"- " + dto.getEnd()+ "h");
            txtPrice.setText("$"+dto.getPrice());
            gridLayout.addView(txtTime);
            gridLayout.addView(txtPrice);
        }

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserDAO userDAO = new UserDAO();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null) {
                    userDAO.deleteCartItem(user.getUid(), cartItemDTO.getID()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                cart.remove(cartItemDTO);
                                CartAdapter.this.notifyDataSetChanged();
                            }else {
                                Toast.makeText(btnRemove.getContext(), "Remove fail", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        return rowView;
    }
}
