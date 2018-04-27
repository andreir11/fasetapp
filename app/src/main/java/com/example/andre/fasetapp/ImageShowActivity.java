package com.example.andre.fasetapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import static android.provider.Telephony.Mms.Part.TEXT;

public class ImageShowActivity extends AppCompatActivity {


    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";
    public ImageView textDisplay;
    public Button buttonToSecond;
    public TextView holdtext;
    private String position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String test = "";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_show);
        buttonToSecond = (Button) findViewById(R.id.button6);
        textDisplay = (ImageView) findViewById(R.id.ImageView40);
        holdtext = (TextView) findViewById(R.id.text1);
        //Intent i = getIntent();
        if (getIntent().getExtras() == null) {
            loadData();
            holdtext.setText(position);

            //saveData();
            buttonToSecond.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveData();
                    startActivity(new Intent(ImageShowActivity.this, SecondActivity.class));
                }
            });


        } else

        {
            position = getIntent().getExtras().getString("url");
            holdtext.setText(position);
            //textDisplay.setText(position);
            Glide.with(getApplicationContext()).load(position).into(textDisplay);
            //position = holdtext.getText().toString();
            //Glide.with(getApplicationContext()).load(position).into(textDisplay);
            buttonToSecond.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(ImageShowActivity.this, SecondActivity.class));
                    //saveData();
                }
            });


        }
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TEXT, holdtext.getText().toString());
        editor.apply();


    }

    public void loadData() {
        saveData();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        sharedPreferences.getString(TEXT, "");
        position = holdtext.getText().toString();
        Glide.with(getApplicationContext()).load(position).into(textDisplay);
    }


}


