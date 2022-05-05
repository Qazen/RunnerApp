package com.example.runnerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.security.Permission;

public class MapsActivity extends AppCompatActivity implements LocationListener {

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    TextView txtLat;
    String lat;
    String provider;
    protected String latitude,longitude;
    protected boolean gps_enabled,network_enabled;

    private Location lastLocation;

    private FragmentMaps fragmentMaps;

    private boolean isRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 1);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            Log.d("Error", "lack of user permission");
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        fragmentMaps = FragmentMaps.getInstance();

        lastLocation = new Location("");
        lastLocation.setLatitude(0.0d);
        lastLocation.setLongitude(0.0d);

        isRunning = false;
        //lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);//change to impossible location
        //fragmentMaps.setMarker(lastLocation.getLatitude(), lastLocation.getLongitude());
    }

    @Override
    public void onLocationChanged(Location location) {
        if (isRunning)
        {
            if (location.getLatitude() != lastLocation.getLatitude() || location.getLongitude() != lastLocation.getLongitude())
            {
                fragmentMaps.setMarker(location.getLatitude(), location.getLongitude());
                lastLocation = location;
            }
        }



        //txtLat = (TextView) findViewById(R.id.textview1);
        //txtLat.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }

    public void startStopButton(View view)
    {
        if (!isRunning)
        {
            isRunning = true;
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                Log.d("Error", "lack of user permission");
                return;
            }
            Location lastKnownLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            fragmentMaps.setMarker(lastKnownLoc.getLatitude(), lastKnownLoc.getLongitude());
        }
    }
}