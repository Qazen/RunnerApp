package com.example.runnerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void loginButton(View view)
    {
        Log.d("MainActivity", "login button clicked");
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}