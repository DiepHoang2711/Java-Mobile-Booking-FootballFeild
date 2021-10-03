package com.example.football_field_booking.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.football_field_booking.OwnerFootballFieldDetailActivity;
import com.example.football_field_booking.OwnerHomeActivity;
import com.example.football_field_booking.R;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class FieldAdapter extends RecyclerView.Adapter<FieldAdapter.FieldViewHolder> {

    private Context context;
    private List<FootballFieldDTO> fieldDTOList;
    private OwnerHomeActivity ownerHomeActivity;

    public FieldAdapter(Context context) {
        this.context = context;
    }

    public void setFieldDTOList(List<FootballFieldDTO> fieldDTOList) {
        this.fieldDTOList = fieldDTOList;
        notifyDataSetChanged();
    }

    public FieldAdapter(Context ct, List<FootballFieldDTO> fieldDTOList) {
        this.context = ct;
        this.fieldDTOList = fieldDTOList;
    }

    @NonNull
    @Override
    public FieldViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_football_field_owner, parent, false);

        FieldViewHolder viewHolder = new FieldViewHolder(view);
        viewHolder.setOnClickListener(new FieldViewHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                Intent intent=new Intent(ownerHomeActivity, OwnerFootballFieldDetailActivity.class);
                intent.putExtra("fieldID",fieldDTOList.get(postion).getFieldID());
                ownerHomeActivity.startActivity(intent);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FieldViewHolder holder, int position) {
        FootballFieldDTO footballFieldDTO = fieldDTOList.get(position);
        if (footballFieldDTO == null) {
            return;
        }
        StorageReference storageRef;
        storageRef = FirebaseStorage.getInstance().getReference("football_field_images").child(footballFieldDTO.getFieldID() + ".jpg");
        storageRef.getDownloadUrl()
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            Glide.with(holder.imgFootballField.getContext())
                                    .load(storageRef)
                                    .into(holder.imgFootballField);
                        }
                    }
                });

        holder.txtFieldName.setText(footballFieldDTO.getName());
        holder.txtLocation.setText(footballFieldDTO.getLocation());
        holder.txtType.setText(footballFieldDTO.getType());
    }

    @Override
    public int getItemCount() {
        if (fieldDTOList != null) {
            return fieldDTOList.size();
        }
        return 0;
    }

    public static class FieldViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgFootballField;
        private TextView txtFieldName;
        private TextView txtLocation;
        private TextView txtType;


        public FieldViewHolder(@NonNull View itemView) {
            super(itemView);

            imgFootballField = itemView.findViewById(R.id.imgFootBallField);
            txtFieldName = itemView.findViewById(R.id.txtFeildName);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            txtType = itemView.findViewById(R.id.txtType);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickListener.onItemClick(view, getAdapterPosition());
                }
            });
        }

        private FieldViewHolder.ClickListener mClickListener;

        public interface ClickListener {
            void onItemClick(View view, int postion);
        }

        public void setOnClickListener(FieldViewHolder.ClickListener listener) {
            mClickListener = listener;
        }

    }
}
