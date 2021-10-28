package com.example.football_field_booking.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.football_field_booking.R;
import com.example.football_field_booking.daos.FootballFieldDAO;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MapSearchNearMeFragment extends Fragment {

    private FusedLocationProviderClient fusedLocationProviderClient;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            FootballFieldDAO fieldDAO = new FootballFieldDAO();

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), getResources().getString(R.string.something_went_wrong),
                        Toast.LENGTH_SHORT).show();
                getActivity().finish();
                return;
            }

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    if (location != null) {
                        try {
                            double lat = location.getLatitude();
                            double lng = location.getLongitude();
                            GeoLocation geoMe = new GeoLocation(lat, lng);
                            double radiusInM = 50 * 1000;

                            List<Task<QuerySnapshot>> tasks = fieldDAO.searchNearMe(geoMe, radiusInM);

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
                                                        double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, geoMe);
                                                        if (distanceInM <= radiusInM) {
                                                            LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                                                            //create maker options
                                                            MarkerOptions options = new MarkerOptions().position(latLng).title(fieldDTO.getName());
                                                            //Zoom map
                                                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                                                            //Add marker to map
                                                            Marker marker = googleMap.addMarker(options);
                                                        }
                                                    }
                                                }

                                            }catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                        }catch (Exception e) {
                            e.printStackTrace();
                        }

                    }else {
                        Toast.makeText(getActivity(), "Fail to get your location", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map_search_near_me, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}