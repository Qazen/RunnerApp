package com.example.runnerapp;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private File runsFile;
    String username;
    ArrayList<String> historyInLines;
    LinearLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Intent intent = getIntent();
        username = intent.getStringExtra(MainActivity.USERNAME);
        runsFile = new File(getApplicationContext().getFilesDir(), username + ".txt");
        try
        {
            runsFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mLayout = (LinearLayout)findViewById(R.id.historyLayout);
        //loadHistory();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.d("Start:", "entered");
//        TextView textView = new TextView(this);
//        textView.setText("aaabbb");
//        mLayout.addView(textView);
        loadHistory();
//        TextView textView2 = new TextView(this);
//        textView2.setText("ccddaa");
//        mLayout.addView(textView2);
    }

    private void loadHistory()
    {
        Log.d("loadHistoryFor:", "entered1");
        historyInLines = getFileContentInLines(runsFile);
        Log.d("loadHistoryFor:", "entered2");
        for (int i=0; i<historyInLines.size(); i++)
        {
            if (historyInLines.get(i).equals("#"))
            {
                Log.d("loadHistoryForIf:", "entered");
                String date = historyInLines.get(i+1);
                String time = historyInLines.get(i+2);
                String distance = historyInLines.get(i+3);
                String speed = historyInLines.get(i+4);

                TextView textView = new TextView(this);
                textView.setText(date+"\n"+time+"\n"+distance+"\n"+speed);
                textView.setBackgroundResource(R.drawable.rectangle);
                textView.setPadding(1, 1, 1, 1);
                textView.setGravity(Gravity.CENTER);
                System.out.println(date);
                mLayout.addView(textView);

                TextView view = new TextView(this);
                view.setBackgroundColor(5);
                view.setLayoutParams(new LinearLayout.LayoutParams(50, 1));

                mLayout.addView(view);
            }
        }
    }

    private ArrayList<String> getFileContentInLines(File file)
    {
        Log.d("getFileContentInLines:", "entered");
        FileInputStream in = null;
        try
        {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
            Log.d("getFileContentInLines:", "not found");
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String strLine;
        ArrayList<String> lines = new ArrayList<String>();
        Log.d("getFileContentInLines:", "after");
//        try {
//            while ((strLine = reader.readLine()) != null) {
//                String lastWord = strLine.substring(strLine.lastIndexOf(" ")+1);
//                lines.add(lastWord);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.d("getFileContentInLines:", "not found 2");
//        }

        try {
            while ((strLine = reader.readLine()) != null) {
                lines.add(strLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("getFileContentInLines:", "not found 2");
        }

        Log.d("getFileContentInLines:", "after2");
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("getFileContentInLines:", "not found 3");
        }
        Log.d("getFileContentInLines:", "after3");

        for (int i=0; i<lines.size(); i++)
        {
            Log.d("lines:", lines.get(i).toString());

        }
        Log.d("getFileContentInLines:", "after4");
        return lines;


    }
}