package com.example.andre.fasetapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class photoInfoInsert extends AppCompatActivity {

    private TextView info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
         info = (TextView)findViewById(R.id.textView2);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_info_insert);
    }
}
