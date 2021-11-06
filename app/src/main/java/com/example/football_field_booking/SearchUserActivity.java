package com.example.football_field_booking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.football_field_booking.adapters.UserAdapter;
import com.example.football_field_booking.daos.FootballFieldDAO;
import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.UserDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchUserActivity extends AppCompatActivity {

    private ListView lvUser;
    private UserAdapter userAdapter;
    private List<UserDTO> userDTOList;
    private EditText edtSearch;
    private String role;
    private TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        edtSearch = findViewById(R.id.edtSearch);
        lvUser = findViewById(R.id.lvUser);
        txtTitle=findViewById(R.id.txtTitle);

        edtSearch.requestFocus();
        edtSearch.setMaxLines(1);

        Intent intent = this.getIntent();
        role = intent.getStringExtra("role");
        if (role != null) {
            if(role.equals("")){
                Toast.makeText(this, getResources().getString(R.string.something_went_wrong)
                        , Toast.LENGTH_SHORT).show();
                finish();
            }else {
                searchByRole();
            }
        }

        lvUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    UserDTO dto = (UserDTO) lvUser.getItemAtPosition(i);
                    Intent intent = new Intent(SearchUserActivity.this, EditProfileByAdminActivity.class);
                    intent.putExtra("userID", dto.getUserID());
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userDTOList = new ArrayList<>();
        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
            UserDTO dto = snapshot.get("userInfo", UserDTO.class);
            if (!user.getUid().equals(dto.getUserID())) {
                userDTOList.add(dto);
            }
        }
        userAdapter = new UserAdapter(SearchUserActivity.this, userDTOList);
        lvUser.setAdapter(userAdapter);
    }

    private void searchByLikeName () throws Exception {
        UserDAO userDAO = new UserDAO();
        String searchName = edtSearch.getText().toString().trim();
        userDAO.searchByLikeNameForAdmin(searchName)
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        loadSearchData(queryDocumentSnapshots);
                        if (queryDocumentSnapshots.size() > 0) {
                            txtTitle.setText("Result of: " + searchName);
                        } else {
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

    private void searchByRole() {
        UserDAO userDAO = new UserDAO();
        userDAO.searchByRole(role).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot queryDocumentSnapshots = task.getResult();
                    if(queryDocumentSnapshots.size() > 0){
                        txtTitle.setText("Role: "+role);
                    }else {
                        txtTitle.setText("--Empty--");
                    }
                    loadSearchData(queryDocumentSnapshots);
                }else {
                    task.getException().printStackTrace();
                }
            }
        });
    }

    public void clickToSearchByLikeName(View view) {
        try{
            searchByLikeName();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void clickToBack(View view) {
        onBackPressed();
    }
}