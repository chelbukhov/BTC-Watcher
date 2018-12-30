package com.example.alex_.bitcoinwatcher;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity {


    private TextView settings;
    private String myStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        settings = findViewById(R.id.txtSettings);
        myStr = "some text";
        settings.setText(myStr);


    }
}
