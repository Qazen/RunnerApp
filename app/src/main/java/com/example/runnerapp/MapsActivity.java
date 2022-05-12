package com.example.runnerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends AppCompatActivity implements LocationListener {

    protected LocationManager locationManager;
    private Location lastLocation;
    private FragmentMaps fragmentMaps;
    private boolean isRunning;
    private boolean isPaused;
    private boolean isShowingStats;
    private ArrayList<LatLng> listOfPoints;

    private Button startStopButton;
    private Button pauseResumeButton;
    private Button historyStatsButton;
    private TextView statsTextView;

    String statsText;
    Timer timer;
    int counter;
    int seconds;
    int minutes;
    int hours;
    float distance;//in meters
    float speed;//in km/h

    private File runsFile;

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

        lastLocation = null;

        isRunning = false;
        isPaused = false;
        isShowingStats = false;
        listOfPoints = new ArrayList<>();

        startStopButton = (Button) findViewById(R.id.startStopButton);
        pauseResumeButton = (Button) findViewById(R.id.pauseResumeButton);
        historyStatsButton = (Button) findViewById(R.id.historyStatsButton);
        statsTextView = (TextView) findViewById(R.id.statsTextView);
        statsTextView.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        String username = intent.getStringExtra(MainActivity.USERNAME);
        runsFile = new File(getApplicationContext().getFilesDir(), username + ".txt");
    }

    @Override
    public void onLocationChanged(Location location) {
        if (isRunning && !isPaused)
        {
            if (lastLocation == null || location.getLatitude() != lastLocation.getLatitude() || location.getLongitude() != lastLocation.getLongitude())
            {
                if (lastLocation != null)
                {
                    distance += lastLocation.distanceTo(location);
                }
                lastLocation = location;
                listOfPoints.add(new LatLng(location.getLatitude(), location.getLongitude()));
                fragmentMaps.drawPath(listOfPoints);
            }
        }
        if (location != null && !isPaused)
        {
            lastLocation = location;
            fragmentMaps.setMarker(location.getLatitude(), location.getLongitude());
        }
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
        isRunning = !isRunning;
        if (isRunning)
        {
            startStopButton.setText("Stop");
            historyStatsButton.setText("Stats");
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                Log.d("Error", "lack of user permission");
                return;
            }
            //Location lastKnownLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //fragmentMaps.setMarker(lastKnownLoc.getLatitude(), lastKnownLoc.getLongitude());
            //listOfPoints.add(new LatLng(lastKnownLoc.getLatitude(), lastKnownLoc.getLongitude()));

            startStatsUpdater();
        }
        else
        {
            startStopButton.setText("Start");
            historyStatsButton.setText("History");
            saveRunData();
        }
    }

    private void saveRunData()
    {
        try (FileWriter fw = new FileWriter(runsFile, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw))
        {
            out.println("#");
            out.println(Calendar.getInstance().getTime().toString());
            out.println(statsText);
            for (int i=0; i<listOfPoints.size(); i++)
            {
                out.print(listOfPoints.get(i).latitude);
                out.print(listOfPoints.get(i).longitude);
            }
            out.println("$");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pauseResumeButton(View view)
    {
        isPaused = !isPaused;
        if (isPaused)
        {
            pauseResumeButton.setText("Resume");
        }
        else
        {
            pauseResumeButton.setText("Pause");
        }
    }

    public void historyStatsButton(View view)
    {
        if (isRunning)
        {
            isShowingStats = !isShowingStats;
            if (isShowingStats)
            {
                statsTextView.setVisibility(View.VISIBLE);
            }
            else
            {
                statsTextView.setVisibility(View.INVISIBLE);
            }
        }
        else
        {

        }
    }

    private void startStatsUpdater()
    {
        timer = new Timer();
        counter = 0;
        seconds = 0;
        minutes = 0;
        hours = 0;
        distance = 0;

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (isRunning && !isPaused)
                        {
                            counter++;
                            seconds = counter%60;
                            minutes = (counter/60)%60;
                            hours = counter/60/60;
                            speed = 1.0f * distance * 60 * 60 / 1000 / counter;
                            statsText = "Time elapsed: "
                                    + String.format(java.util.Locale.US, "%02d", hours)
                                    + ":"
                                    + String.format(java.util.Locale.US, "%02d", minutes)
                                    + ":"
                                    + String.format(java.util.Locale.US, "%02d", seconds)
                                    + "\n"
                                    + "Distance moved: "
                                    + String.format(java.util.Locale.US, "%.0f", distance)
                                    + " meters"
                                    + "\n"
                                    + "Approximate speed: "
                                    + String.format(java.util.Locale.US, "%.2f", speed)
                                    + " km/h";
                        }
                        statsTextView.setText(statsText);
                    }
                });
            }
        }, 1000, 1000);
    }

}