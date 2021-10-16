package com.example.football_field_booking;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.football_field_booking.adapters.TimePickerDetailAdapter;
import com.example.football_field_booking.daos.FootballFieldDAO;
import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.CartItemDTO;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.example.football_field_booking.dtos.TimePickerDTO;
import com.example.football_field_booking.dtos.UserDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FootballFieldDetailActivity extends AppCompatActivity {

    private TextView txtSelectDate;
    private DatePickerDialog datePickerDialog;
    private int year, month, day;
    private Calendar calendar;
    private String now;
    private FloatingActionButton btnBack;
    private Button btnAddToCart;
    private LinearLayout groupBtnOwner;
    private TextView txtFieldName,txtLocation,txtRate,txtType;
    private List<TimePickerDTO> timePickerDTOList;
    private TimePickerDetailAdapter timePickerDetailAdapter;
    private ListView lvTimePickerDetail;
    private FootballFieldDTO fieldDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onwer_football_field_detail);

        calendar = Calendar.getInstance();
        txtSelectDate = findViewById(R.id.txtSelectDate);
        btnBack = findViewById(R.id.btnBack);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        groupBtnOwner = findViewById(R.id.groupBtnOwner);
        txtFieldName=findViewById(R.id.txtFieldName);
        txtLocation=findViewById(R.id.txtLocation);
        txtRate=findViewById(R.id.txtRate);
        txtType=findViewById(R.id.txtType);
        lvTimePickerDetail = findViewById(R.id.lvTimePickerDetail);

        now = calendar.get(Calendar.DAY_OF_MONTH) +
                "/" + (calendar.get(Calendar.MONTH) + 1) +
                "/" + calendar.get(Calendar.YEAR);
        txtSelectDate.setText(now);
        System.out.println("Now:" + now);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    addToCart();
                }catch (Exception e) {
                    e.printStackTrace();
                }
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

                            } else if(role.equals("user")){
                                btnAddToCart.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }

        FootballFieldDAO  fieldDAO=new FootballFieldDAO();
        Intent intent=this.getIntent();
        String fieldID=intent.getStringExtra("fieldID");
        fieldDAO.getFieldByID(fieldID).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                fieldDTO=documentSnapshot.toObject(FootballFieldDTO.class);
                txtFieldName.setText(fieldDTO.getName());
                txtLocation.setText(fieldDTO.getLocation());
                txtRate.setText(fieldDTO.getRate()+"");
                txtType.setText(fieldDTO.getType());

                fieldDAO.getAllTimePickerOfField(fieldDTO.getFieldID())
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                timePickerDTOList = new ArrayList<>();
                                for (QueryDocumentSnapshot snapshot:queryDocumentSnapshots){
                                    timePickerDTOList.add(snapshot.toObject(TimePickerDTO.class));
                                }
                                List<TimePickerDTO> chooseList = new ArrayList<>();
                                List<TimePickerDTO> bookedTimeList = new ArrayList<>();
                                timePickerDetailAdapter = new TimePickerDetailAdapter(FootballFieldDetailActivity.this,timePickerDTOList, chooseList, bookedTimeList);
                                lvTimePickerDetail.setScrollContainer(false);
                                lvTimePickerDetail.setAdapter(timePickerDetailAdapter);
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
        if (!txtSelectDate.getText().equals(now)) {
            day = Integer.parseInt(selectedDate[0]);
            month = Integer.parseInt(selectedDate[1]) - 1;
            year = Integer.parseInt(selectedDate[2]);
        }
        System.out.println(day + "/" + month + "/" + year);
        datePickerDialog = new DatePickerDialog(FootballFieldDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                txtSelectDate.setText(d + "/" + (m + 1) + "/" + y);
            }
        }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }
    
    private void addToCart () throws Exception{
        List<TimePickerDTO> list = timePickerDetailAdapter.getChooseList();
        if(list.isEmpty()){
            Toast.makeText(this, "Please choose at least one time slot", Toast.LENGTH_LONG).show();
        }else {
            float total = 0;
            for (TimePickerDTO dto: list) {
                total += dto.getPrice();
            }
            String fieldID = fieldDTO.getFieldID();
            String name = fieldDTO.getName();
            String location = fieldDTO.getLocation();
            String type = fieldDTO.getType();
            String image = fieldDTO.getImage();
            CartItemDTO cartItemDTO = new CartItemDTO(fieldID, name, location, type, image, "", total, list);
            UserDAO userDAO = new UserDAO();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user != null) {
                userDAO.addToCart(cartItemDTO, user.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(FootballFieldDetailActivity.this, "Add to cart success"
                                    , Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(FootballFieldDetailActivity.this, "Add to cart fail"
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
}