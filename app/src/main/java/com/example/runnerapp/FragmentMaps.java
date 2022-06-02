package com.example.runnerapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class FragmentMaps extends Fragment {

    private static FragmentMaps instance;
    private GoogleMap map;
    private Marker marker;
    private PolylineOptions pathOptions;
    private Polyline polyline;

        private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        instance = this;

        return inflater.inflate(R.layout.fragment_maps, container, false);
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

    public static FragmentMaps getInstance()
    {
        return instance;
    }

    public void setMarker(double latitude, double longitude)//reimplement in maps activity
    {
        LatLng markerPos = new LatLng(latitude, longitude);
        if (marker != null)
        {
            marker.remove();
        }
        marker = map.addMarker(new MarkerOptions().position(markerPos).title("You"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPos, 18.0f));
    }

    //public GoogleMap getMap() {return map;}

    public void drawPath(ArrayList<LatLng> listOfPoints)
    {
        clearPath();
        pathOptions = new PolylineOptions();
        pathOptions.width(5);
        pathOptions.color(Color.RED);
        pathOptions.addAll(listOfPoints);
        polyline = map.addPolyline(pathOptions);
    }

    public void clearPath()
    {
        if (polyline != null)
        {
            polyline.remove();
        }
    }
}