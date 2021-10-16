package com.example.football_field_booking;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.football_field_booking.adapters.TimePickerDetailAdapter;
import com.example.football_field_booking.daos.FootballFieldDAO;
import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.example.football_field_booking.dtos.TimePickerDTO;
import com.example.football_field_booking.dtos.UserDTO;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FootballFieldDetailActivity extends AppCompatActivity {

    private TextView txtSelectDate;
    private DatePickerDialog datePickerDialog;
    private int year, month, day;
    private Calendar calendar;
    private FloatingActionButton btnBack;
    private Button btnAddToCart;
    private LinearLayout groupBtnOwner;
    private TextView txtFieldName, txtLocation, txtRate, txtType;
    private List<TimePickerDTO> timePickerDTOList;
    private TimePickerDetailAdapter timePickerDetailAdapter;
    private ListView lvTimePickerDetail;
    private ImageView imgFootBallField;
    public static final SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onwer_football_field_detail);

        calendar = Calendar.getInstance();
        txtSelectDate = findViewById(R.id.txtSelectDate);
        btnBack = findViewById(R.id.btnBack);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        groupBtnOwner = findViewById(R.id.groupBtnOwner);
        txtFieldName = findViewById(R.id.txtFieldName);
        txtLocation = findViewById(R.id.txtLocation);
        txtRate = findViewById(R.id.txtRate);
        txtType = findViewById(R.id.txtType);
        lvTimePickerDetail = findViewById(R.id.lvTimePickerDetail);
        imgFootBallField = findViewById(R.id.imgFootBallField);

        txtSelectDate.setText(df.format(calendar.getTime()));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            btnAddToCart.setVisibility(View.VISIBLE);
        } else {
            UserDAO userDAO = new UserDAO();
            userDAO.getUserById(user.getUid())
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            UserDTO dto = documentSnapshot.toObject(UserDTO.class);
                            String role = dto.getRole();
                            if (role.equals("owner")) {
                                groupBtnOwner.setVisibility(View.VISIBLE);

                            }
                        }
                    });
        }

        FootballFieldDAO fieldDAO = new FootballFieldDAO();
        Intent intent = this.getIntent();
        String fieldID = intent.getStringExtra("fieldID");
        fieldDAO.getFieldByID(fieldID).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                FootballFieldDTO fieldDTO = documentSnapshot.toObject(FootballFieldDTO.class);
                txtFieldName.setText(fieldDTO.getName());
                txtLocation.setText(fieldDTO.getLocation());
                txtRate.setText(fieldDTO.getRate() + "");
                txtType.setText(fieldDTO.getType());
                if (fieldDTO.getImage() != null) {
                    Uri uri = Uri.parse(fieldDTO.getImage());
                    Glide.with(imgFootBallField.getContext())
                            .load(uri)
                            .into(imgFootBallField);
                }

                fieldDAO.getAllTimePickerOfField(fieldDTO.getFieldID())
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                timePickerDTOList = new ArrayList<>();
                                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                    timePickerDTOList.add(snapshot.toObject(TimePickerDTO.class));
                                }
                                timePickerDetailAdapter = new TimePickerDetailAdapter(FootballFieldDetailActivity.this, timePickerDTOList);
                                lvTimePickerDetail.setAdapter(timePickerDetailAdapter);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    public void clickToChangeDate(View view) {
        String[] selectedDate = txtSelectDate.getText().toString().split("/");

        year = Integer.parseInt(selectedDate[0]);
        month = Integer.parseInt(selectedDate[1]) - 1;
        day = Integer.parseInt(selectedDate[2]);

        datePickerDialog = new DatePickerDialog(FootballFieldDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                Date date = null;
                try {
                    date = df.parse(y + "/" + (m + 1) + "/" + d);

                    txtSelectDate.setText(df.format(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }
}