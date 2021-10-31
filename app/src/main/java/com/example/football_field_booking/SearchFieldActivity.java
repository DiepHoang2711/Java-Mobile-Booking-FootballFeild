package com.example.football_field_booking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.example.football_field_booking.adapters.FootballFieldAdapter;
import com.example.football_field_booking.adapters.TimePickerAdapter;
import com.example.football_field_booking.daos.FootballFieldDAO;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.List;

public class SearchFieldActivity extends AppCompatActivity {

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
                    Intent intent = new Intent(SearchFieldActivity.this, FootballFieldDetailActivity.class);
                    intent.putExtra("fieldID", dto.getFieldID());
                    startActivity(intent);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null &&
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        // the user is done typing.
                        try {
                            searchByLikeName();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                }
                return false;
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
        fieldAdapter = new FootballFieldAdapter(SearchFieldActivity.this, fieldDTOList);
        lvFootballField.setAdapter(fieldAdapter);
    }

    public void clickToSearchByLikeName(View view) {
        try{
            searchByLikeName();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void searchByLikeName () throws Exception {
        FootballFieldDAO fieldDAO = new FootballFieldDAO();
        String searchName = edtSearch.getText().toString().trim();
        fieldDAO.searchByLikeNameForUser(searchName)
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.e("List Size:",queryDocumentSnapshots.size()+"");
                        loadSearchData(queryDocumentSnapshots);
                        txtTitle.setText("Result of: "+searchName);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void searchByTypeField() {
        FootballFieldDAO fieldDAO = new FootballFieldDAO();

            fieldDAO.searchByTypeForUser(type).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    loadSearchData(queryDocumentSnapshots);
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