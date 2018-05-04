package com.example.andre.fasetapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class ShowImageGallery extends AppCompatActivity {

    private ImageView imgContainer;
    private TextView txtAlias;
    String imgLink;
    String imgAlias;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image_gallery);

        imgContainer = (ImageView)findViewById(R.id.imageContainer);
        txtAlias = (TextView)findViewById(R.id.textAlias);
        Intent intent = getIntent();

        imgLink = intent.getStringExtra("link");
        imgAlias = intent.getStringExtra("alias");
        Glide.with(this)
                .load(imgLink)
                .into(imgContainer);
        txtAlias.setText(imgAlias);





    }
}
