package com.example.football_field_booking;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.example.football_field_booking.adapters.FootballFieldAdapter;
import com.example.football_field_booking.daos.FootballFieldDAO;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchByNameActivity extends AppCompatActivity {

    private ListView lvFootballField;
    private FootballFieldAdapter fieldAdapter;
    private List<FootballFieldDTO> fieldDTOList;
    private EditText edtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_name);
        edtSearch=findViewById(R.id.edtSearch);
        lvFootballField=findViewById(R.id.lvFootballField);
        fieldDTOList=new ArrayList<>();

    }

    public void clickToSearchByLikeName(View view) {

        FootballFieldDAO fieldDAO=new FootballFieldDAO();
        try {
            fieldDAO.searchByLikeName(edtSearch.getText().toString().trim())
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot snapshot: queryDocumentSnapshots) {
                        FootballFieldDTO dto = snapshot.get("fieldInfo", FootballFieldDTO.class);
                        fieldDTOList.add(dto);
                    }
                    Log.d("listSearchSize",fieldDTOList.size()+"");
                    fieldAdapter=new FootballFieldAdapter(SearchByNameActivity.this,fieldDTOList);
                    lvFootballField.setAdapter(fieldAdapter);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
    }

    public void clickToBack(View view) {
        onBackPressed();
    }
}