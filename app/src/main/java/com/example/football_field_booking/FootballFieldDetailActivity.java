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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.football_field_booking.adapters.TimePickerDetailAdapter;
import com.example.football_field_booking.daos.FootballFieldDAO;
import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.CartItemDTO;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.example.football_field_booking.dtos.FootballFieldDocument;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FootballFieldDetailActivity extends AppCompatActivity {

    private DatePickerDialog datePickerDialog;
    private int year, month, day;
    private Calendar calendar;
    private FloatingActionButton btnBack;
    private Button btnAddToCart,btnLogin;
    private TextView txtFieldName, txtLocation, txtRate, txtType, txtSelectDate;
    private List<TimePickerDTO> timePickerDTOList;
    private TimePickerDetailAdapter timePickerDetailAdapter;
    private ListView lvTimePickerDetail;
    private ImageView imgFootBallField;
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
    private FootballFieldDTO fieldDTO;
    private CartItemDTO cartItemAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onwer_football_field_detail);

        calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        txtSelectDate = findViewById(R.id.txtSelectDate);
        btnBack = findViewById(R.id.btnBack);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        txtFieldName = findViewById(R.id.txtFieldName);
        txtLocation = findViewById(R.id.txtLocation);
        txtRate = findViewById(R.id.txtRate);
        txtType = findViewById(R.id.txtType);
        lvTimePickerDetail = findViewById(R.id.lvTimePickerDetail);
        imgFootBallField = findViewById(R.id.imgFootBallField);
        btnLogin=findViewById(R.id.btnLogin);

        txtSelectDate.setText(df.format(calendar.getTime()));

        timePickerDTOList = new ArrayList<>();
        cartItemAvailable = new CartItemDTO();

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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserDAO userDAO = new UserDAO();
            userDAO.getUserById(user.getUid())
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            UserDTO dto = documentSnapshot.get("userInfo", UserDTO.class);
                            String role = dto.getRole();
                            if (role.equals("user")) {
                                btnAddToCart.setVisibility(View.VISIBLE);
                                btnLogin.setVisibility(View.GONE);
                            }
                        }
                    });
        }else{
            btnLogin.setVisibility(View.VISIBLE);
            btnAddToCart.setVisibility(View.GONE);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FootballFieldDetailActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }

        Intent intent = this.getIntent();
        String fieldID = intent.getStringExtra("fieldID");
        if (fieldID != null) {
            loadData(fieldID);
        }
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

    private void addToCart() throws Exception {
        List<TimePickerDTO> list = timePickerDetailAdapter.getChooseList();
        if (list.isEmpty()) {
            Toast.makeText(this, "Please choose at least one time slot", Toast.LENGTH_LONG).show();
        } else {

            UserDAO userDAO = new UserDAO();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                userDAO.getUserById(user.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserDTO userDTO = documentSnapshot.get("userInfo", UserDTO.class);
                        float total = 0;
                        for (TimePickerDTO dto : list) {
                            total += dto.getPrice();
                        }
                        String date = txtSelectDate.getText().toString();
                        CartItemDTO cartItemDTO = new CartItemDTO(userDTO, fieldDTO, date, total, list);
                        userDAO.addToCart(cartItemDTO, cartItemAvailable.getID(), user.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    loadData(fieldDTO.getFieldID());
                                    Toast.makeText(FootballFieldDetailActivity.this, "Add to cart success"
                                            , Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(FootballFieldDetailActivity.this, MainActivity.class);
                                    intent.putExtra("action", "add to cart");
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(FootballFieldDetailActivity.this, "Add to cart fail"
                                            , Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    private void loadData(String fieldID) {
        try {
            FootballFieldDAO fieldDAO = new FootballFieldDAO();
            UserDAO userDAO = new UserDAO();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            fieldDAO.getFieldByID(fieldID).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    fieldDTO = documentSnapshot.get("fieldInfo", FootballFieldDTO.class);
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

                    timePickerDTOList = documentSnapshot.toObject(FootballFieldDocument.class).getTimePicker();
                    String date = txtSelectDate.getText().toString();
                    fieldDAO.getBookingOfAFieldByDate(fieldID, date).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<TimePickerDTO> bookedTimeList = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                List<TimePickerDTO> listDto = doc.toObject(CartItemDTO.class).getTimePicker();
                                bookedTimeList.addAll(listDto);
                            }

                            if (user != null) {
                                Log.d("USER", "date is: " + date);
                                userDAO.getItemInCartByFieldAndDate(user.getUid(), fieldDTO.getFieldID(), date).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if (!queryDocumentSnapshots.isEmpty()) {
                                            DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                                            cartItemAvailable = doc.toObject(CartItemDTO.class);
                                            Log.d("USER", "listInCart: " + cartItemAvailable.toString());
                                        }
                                        timePickerDetailAdapter = new TimePickerDetailAdapter(FootballFieldDetailActivity.this, timePickerDTOList, cartItemAvailable.getTimePicker(), bookedTimeList);
                                        lvTimePickerDetail.setScrollContainer(false);
                                        lvTimePickerDetail.setAdapter(timePickerDetailAdapter);

                                    }
                                });
                            } else {
                                timePickerDetailAdapter = new TimePickerDetailAdapter(FootballFieldDetailActivity.this, timePickerDTOList, cartItemAvailable.getTimePicker(), bookedTimeList);
                                lvTimePickerDetail.setScrollContainer(false);
                                lvTimePickerDetail.setAdapter(timePickerDetailAdapter);
                            }

                        }
                    });


                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

//        private List<CartItemDTO> cartItemDTOList;
//        private List<BookingDocument> bookingDocumentList;
//
//        getBookingforUser(user ID).addsusssecc {
//
//            for( bookingDoc: bookingDocs){
//                bookingDTO = bookingDoc.toObject(BookingDTO.class);
//                BookingDocument bookingDocument = new BookingDocumnet();
//                bookingDocument.setbookingDTO(bookingDTO);
//                getAllFieldsInBooking(bookingDoc.getID).addsussecc {
//                    for(doc: docs) {
//                        field = doc.toocject(CartItemDTO.class);
//                        cartItemDTOList.add(field);
//                    }
//                    bookingDocument.setCartItemDTOList(cartItemDTOList);
//                    bookingDocumentList.add(bookingDocument);
//                    adapter.set(bookingDocumentList);
//                    lv.setAdpter(adapter);
//                }
//            }
//
//
//        }


    }
}
