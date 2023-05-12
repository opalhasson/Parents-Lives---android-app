package com.example.HW2.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.HW2.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class Fragment_GoogleMap extends Fragment {

        private GoogleMap mMap;
        private TextView locationDetails ;
        private OnMapReadyCallback callback = googleMap -> { mMap = googleMap; };

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Initialize view
            View view = inflater.inflate(R.layout.fragment__google_map,container,false);
            findViews(view);
            // Initialize map fragment
            SupportMapFragment supportMapFragment = (SupportMapFragment)
                    getChildFragmentManager().findFragmentById(R.id.google_map);
            //Async
            supportMapFragment.getMapAsync(callback);
            return view;
        }


        private void findViews(View view) {
            locationDetails = view.findViewById(R.id.data);
        }


        public void locateOnMap(double x, double y) {
            if (x==0.0 && y==0.0) {
                locationDetails.setVisibility(View.VISIBLE);
                locationDetails.setText("No information about location");
            }
            else {
                locationDetails.setVisibility(View.INVISIBLE);
                LatLng point = new LatLng(x, y);
                mMap.addMarker(new MarkerOptions().position(point).title(""));
                moveToCurrentLocation(point);
            }
        }

        private void moveToCurrentLocation(LatLng currentLocation)
        {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
            // Zoom in, animating the camera.
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
            // Zoom out to zoom level 10, animating with a duration of 2 seconds.
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);

        }


    }