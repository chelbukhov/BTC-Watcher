package com.example.alex_.bitcoinwatcher;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {


    private String strAbout;
    private TextView about;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        about = findViewById(R.id.txtAbout);
        strAbout = "About program." + "\n" + "autor: Chelbukhov Aleksey" + "\n" + "2018";
        about.setText(strAbout);
    }
}
