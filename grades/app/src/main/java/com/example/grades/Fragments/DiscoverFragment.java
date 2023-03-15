package com.example.grades.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.grades.Activities.DisplayActivity;
import com.example.grades.Models.Station;
import com.example.grades.R;
import com.example.grades.Misc.SharedViewModel;
import com.example.grades.Misc.SingleShotLocationProvider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class DiscoverFragment extends Fragment {

    private GoogleMap mMap;
    SharedViewModel sharedViewModel;
    ArrayList<Station> stations;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_discover, container, false);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getStations().observe(getViewLifecycleOwner(), item -> {
            stations = item;
            SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map2);
            supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    SingleShotLocationProvider.requestSingleUpdate(getContext(),
                            new SingleShotLocationProvider.LocationCallback() {
                                @Override public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.latitude,location.longitude),11.0f));
                                }
                            });
                    for(Station station : stations) {
                        googleMap.addMarker(new MarkerOptions().position(new LatLng(station.getCoordinate().getLat(),station.getCoordinate().getLon())).title(station.getName()));
                    }
                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            Context context = v.getContext();
                            Intent intent = new Intent(context, DisplayActivity.class);
                            for(int i = 0; i < stations.size(); i++) {
                                if(stations.get(i).getName().equals(marker.getTitle())) {
                                    intent.putExtra("list",stations);
                                    intent.putExtra("pos",i);
                                    intent.putExtra("type", "add");
                                    context.startActivity(intent);
                                    break;
                                }
                            }
                        }
                    });
                }
            });
        });

        return v;
    }
}