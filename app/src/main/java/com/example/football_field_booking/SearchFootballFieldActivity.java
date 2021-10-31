package com.example.football_field_booking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.example.football_field_booking.adapters.FootballFieldAdapter;
import com.example.football_field_booking.daos.FootballFieldDAO;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.List;

public class SearchFootballFieldActivity extends AppCompatActivity {

    private ListView lvFootballField;
    private FootballFieldAdapter fieldAdapter;
    private List<FootballFieldDTO> fieldDTOList;
    private EditText edtSearch;
    private String type;
    private TextView txtTitle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_field);
        edtSearch = findViewById(R.id.edtSearch);
        lvFootballField = findViewById(R.id.lvFootballField);
        txtTitle=findViewById(R.id.txtTitle);

        edtSearch.requestFocus();
        edtSearch.setMaxLines(1);
//        edtSearch.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
//                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                    // Perform action on key press
//                    clickToSearchByLikeName(view);
//                    return true;
//                }
//                return false;
//            }
//        });

        Intent intent = this.getIntent();
        type = intent.getStringExtra("typeField");
        if (type != null) {
            txtTitle.setText("Field Type: "+type);
            searchByTypeField();
        }

        lvFootballField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    FootballFieldDTO dto = (FootballFieldDTO) lvFootballField.getItemAtPosition(i);
                    Intent intent = new Intent(SearchFootballFieldActivity.this, FootballFieldDetailActivity.class);
                    intent.putExtra("fieldID", dto.getFieldID());
                    startActivity(intent);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadSearchData(QuerySnapshot queryDocumentSnapshots) {
        fieldDTOList=new ArrayList<>();
        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
            FootballFieldDTO dto = snapshot.get("fieldInfo", FootballFieldDTO.class);
            if(dto.getStatus().equals("active")){
                fieldDTOList.add(dto);
            }
        }
        fieldAdapter = new FootballFieldAdapter(SearchFootballFieldActivity.this, fieldDTOList);
        lvFootballField.setAdapter(fieldAdapter);
    }

    public void clickToSearchByLikeName(View view) {
        FootballFieldDAO fieldDAO = new FootballFieldDAO();
        String searchName = edtSearch.getText().toString().trim();
        try{
            fieldDAO.searchByLikeNameForUser(searchName)
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            loadSearchData(queryDocumentSnapshots);
                            if(queryDocumentSnapshots.size()>0){
                                txtTitle.setText("Result of: "+searchName);
                            }else{
                                txtTitle.setText("--Empty--");
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void searchByTypeField() {
        FootballFieldDAO fieldDAO = new FootballFieldDAO();

            fieldDAO.searchByTypeForUser(type).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    loadSearchData(queryDocumentSnapshots);
                    if(queryDocumentSnapshots.size()==0){
                        txtTitle.setText("--Empty--");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
    }

    public void clickToBack(View view) {
        onBackPressed();
    }
}