package com.example.football_field_booking.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.football_field_booking.FootballFieldDetailActivity;
import com.example.football_field_booking.MainActivity;
import com.example.football_field_booking.R;
import com.example.football_field_booking.adapters.FootballFieldAdapter;
import com.example.football_field_booking.daos.FootballFieldDAO;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserHomeFragment extends Fragment {

    public static final int RC_PERMISSTION_LOCATION = 1001;
    public static final int RADIUS_IN_M = 50 * 1000;
    private ListView lvFootballField, lvFieldNearMe;
    private List<FootballFieldDTO> fieldDTOList, fieldNearMeList;
    private FootballFieldAdapter fieldAdapter, fieldNearMeAdapter;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private TextView ttxErrorNearMe;
    private List<Double> distanceList;
    private CardView cardFieldNearME;

    public UserHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);

        lvFootballField=view.findViewById(R.id.lvFootballField);
        lvFieldNearMe = view.findViewById(R.id.lvFieldNearME);
        ttxErrorNearMe = view.findViewById(R.id.txtErrorNearMe);
        cardFieldNearME = view.findViewById(R.id.cardFieldNearME);
        fieldNearMeList = new ArrayList<>();
        distanceList = new ArrayList<>();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        lvFootballField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    FootballFieldDTO dto = (FootballFieldDTO) lvFootballField.getItemAtPosition(i);
                    Intent intent = new Intent(getActivity(), FootballFieldDetailActivity.class);
                    intent.putExtra("fieldID", dto.getFieldID());
                    startActivity(intent);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        lvFieldNearMe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FootballFieldDTO dto = (FootballFieldDTO) lvFieldNearMe.getItemAtPosition(i);
                Intent intent = new Intent(getActivity(), FootballFieldDetailActivity.class);
                intent.putExtra("fieldID", dto.getFieldID());
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, RC_PERMISSTION_LOCATION);
        } else {
            loadDataNearMe();
        }

    }

    private void loadData () {
        fieldDTOList = new ArrayList<>();
        FootballFieldDAO dao = new FootballFieldDAO();
        dao.getAllFootballField().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                try {
                    for (QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                        Log.d("USER", "DocID: " + doc.getId());
                        Log.d("USER", "Doc: " + doc.get("fieldInfo", FootballFieldDTO.class));
                        FootballFieldDTO dto = doc.get("fieldInfo", FootballFieldDTO.class);
                        fieldDTOList.add(dto);
                    }
                    fieldAdapter = new FootballFieldAdapter(getActivity(), fieldDTOList);
                    lvFootballField.setAdapter(fieldAdapter);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadDataNearMe () {
        try {
            MainActivity mainActivity = (MainActivity) getActivity();
            GeoPoint geoMe = mainActivity.getGeoMe();

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ttxErrorNearMe.setVisibility(View.VISIBLE);
                return;
            }

            if(geoMe == null) {
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location != null) {
                            try {
                                double lat = location.getLatitude();
                                double lng = location.getLongitude();
                                GeoLocation geoLocation = new GeoLocation(lat, lng);
                                searchNearMe(geoLocation);
                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            ttxErrorNearMe.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }else {
                GeoLocation geoLocation = new GeoLocation(geoMe.getLatitude(), geoMe.getLongitude());
                searchNearMe(geoLocation);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void searchNearMe (GeoLocation geoLocation) throws Exception{
        FootballFieldDAO fieldDAO = new FootballFieldDAO();
        List<Task<QuerySnapshot>> tasks = fieldDAO.searchNearMe(geoLocation, RADIUS_IN_M);
        fieldNearMeList.clear();
        // Collect all the query results together into a single list
        Tasks.whenAllComplete(tasks)
                .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Task<?>>> t) {
                        try {

                            for (Task<QuerySnapshot> task : tasks) {
                                QuerySnapshot snap = task.getResult();
                                for (DocumentSnapshot doc : snap.getDocuments()) {
                                    FootballFieldDTO fieldDTO = doc.get("fieldInfo", FootballFieldDTO.class);
                                    GeoPoint geoPoint = fieldDTO.getGeoPoint();
                                    double lat = geoPoint.getLatitude();
                                    double lng = geoPoint.getLongitude();

                                    // We have to filter out a few false positives due to GeoHash
                                    // accuracy, but most will match
                                    GeoLocation docLocation = new GeoLocation(lat, lng);
                                    double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, geoLocation);
                                    if (distanceInM <= RADIUS_IN_M) {
                                        distanceInM = distanceInM/1000;
                                        distanceInM = Math.round(distanceInM*10.0)/10.0;
                                        fieldNearMeList.add(fieldDTO);
                                        distanceList.add(distanceInM);
                                    }
                                }
                            }

                            loadDataNearMeToListView();

                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void loadDataNearMeToListView () {
        if(fieldNearMeList.isEmpty()){
            ttxErrorNearMe.setVisibility(View.VISIBLE);
        } else {
            ttxErrorNearMe.setVisibility(View.GONE);
            cardFieldNearME.setVisibility(View.VISIBLE);
            Log.d("USER", "list: " + fieldNearMeList.toString());
            fieldNearMeAdapter = new FootballFieldAdapter(getActivity(), fieldNearMeList);
            fieldNearMeAdapter.setDistanceList(distanceList);
            lvFieldNearMe.setAdapter(fieldNearMeAdapter);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == RC_PERMISSTION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadDataNearMe();
            } else {
                ttxErrorNearMe.setVisibility(View.VISIBLE);
                loadDataNearMe();
            }
        }
    }
}