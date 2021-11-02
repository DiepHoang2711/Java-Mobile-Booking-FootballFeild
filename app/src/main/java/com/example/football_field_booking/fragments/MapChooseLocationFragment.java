package com.example.football_field_booking.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.football_field_booking.CreateFootballFieldActivity;
import com.example.football_field_booking.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapChooseLocationFragment extends Fragment {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng marker;
    private String locationName;
    private Button btnSubmit;

    public  MapChooseLocationFragment () {

    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            Log.d("USER", "onMapReady");

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), getResources().getString(R.string.something_went_wrong),
                        Toast.LENGTH_SHORT).show();
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
                return;
            }

            LocationRequest mLocationRequest = LocationRequest.create();
            mLocationRequest.setInterval(60000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationCallback mLocationCallback = new LocationCallback() {};

            fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    if (location != null) {
                        try {
                            Geocoder geocoder = new Geocoder(getContext());
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),
                                    location.getLongitude(), 1);
                            Address address = addresses.get(0);

                            locationName = address.getAddressLine(0);

                            Log.d("USER", "location: " + locationName);

                            marker = new LatLng(location.getLatitude(),
                                    location.getLongitude());
                            //create maker options
                            MarkerOptions options = new MarkerOptions().position(marker).title("i am here");
                            //Zoom map
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker, 15));
                            //Add marker to map
                            googleMap.addMarker(options);
                        }catch (Exception e) {
                            e.printStackTrace();
                        }

                    }else {
                        Toast.makeText(getActivity(), "Fail to get your location", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng latLng) {
                    try {
                        marker = latLng;
                        Geocoder geocoder = new Geocoder(getContext());
                        List<Address> addresses = geocoder.getFromLocation(marker.latitude,
                                marker.longitude, 1);
                        Address address = addresses.get(0);

                        locationName = address.getAddressLine(0);
                        Log.d("USER", "location: " + locationName);

                        // Clear previously click position.
                        googleMap.clear();
                        //create maker options
                        MarkerOptions options = new MarkerOptions().position(marker).title("i am here");
                        //Zoom map
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker, 15));
                        //Add marker to map
                        googleMap.addMarker(options);
                    }catch (Exception e) {
                        e.printStackTrace();
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
        View view = inflater.inflate(R.layout.fragment_map_choose_location, container, false);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (marker != null) {
                    try {
                        Intent intent = getActivity().getIntent();
                        intent.putExtra("lat", marker.latitude);
                        intent.putExtra("lng", marker.longitude);
                        intent.putExtra("locationName", locationName);
                        getActivity().setResult(Activity.RESULT_OK, intent);
                        getActivity().finish();

                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please choose your location", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
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