package com.example.runnerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private File usersFile;
    private AdView mAdView;

    public static final String USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usersFile = new File(getApplicationContext().getFilesDir(), "users.txt");
        setContentView(R.layout.activity_main);


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void registerButton(View view)
    {
        Log.d("MainActivity", "register button clicked");
        final EditText editTextUsername =  (EditText) findViewById(R.id.editTextUsername);
        final EditText editTextPassword =  (EditText) findViewById(R.id.editTextPassword);
        final String username = editTextUsername.getText().toString();
        final String password = editTextPassword.getText().toString();
        if (registerUser(username, password))
        {
            startAppAs(username);
        }
        else
        {
            generateErrorToast("Username already taken");
        }
    }

    private boolean registerUser(String username, String password)
    {
        try
        {
            usersFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (checkIfUserExists(username))
        {
            return false;//error, user already exists
        }
        addUser(username, password);

        return true;
    }

    private void addUser(String username, String password)
    {
        try (FileWriter fw = new FileWriter(usersFile, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw))
        {
            out.println(username);
            out.println(password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkIfUserExists(String username)
    {
        ArrayList<String> usersFileContent = getFileContentInLines(usersFile);

        for (int i=0; i<usersFileContent.size(); i=i+2)
        {
            if (usersFileContent.get(i).equals(username))
            {
                return true;
            }
        }
        return false;
    }

    private ArrayList<String> getFileContentInLines(File file)
    {
        FileInputStream in = null;
        try
        {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String strLine;
        ArrayList<String> lines = new ArrayList<String>();
        try {
            while ((strLine = reader.readLine()) != null) {
                String lastWord = strLine.substring(strLine.lastIndexOf(" ")+1);
                lines.add(lastWord);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public void loginButton(View view)
    {
        Log.d("MainActivity", "login button clicked");
        final EditText editTextUsername =  (EditText) findViewById(R.id.editTextUsername);
        final EditText editTextPassword =  (EditText) findViewById(R.id.editTextPassword);
        final String username = editTextUsername.getText().toString();
        final String password = editTextPassword.getText().toString();
        if (loginUser(username, password))
        {
            startAppAs(username);
        }
        else
        {
            generateErrorToast("Invalid credentials");
        }
    }

    private boolean loginUser(String username, String password)
    {
        try
        {
            usersFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!checkIfCredentialsAreCorrect(username, password))
        {
            return false;//error, invalid credentials
        }
        return true;
    }

    private boolean checkIfCredentialsAreCorrect(String username, String password)
    {
        ArrayList<String> usersFileContent = getFileContentInLines(usersFile);

        for (int i=0; i<usersFileContent.size(); i=i+2)
        {
            if (usersFileContent.get(i).equals(username) && usersFileContent.get(i+1).equals(password))
            {
                return true;
            }
        }
        return false;
    }

    private void startAppAs(String username)
    {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(USERNAME, username);
        startActivity(intent);
    }

    private void generateErrorToast(String message)
    {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
}