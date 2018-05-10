package com.example.andre.fasetapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShowImageGalleryOfCollage extends AppCompatActivity {

    private ImageView imgContainer;
    private TextView txtAlias;
    private TextView imageTextSleeve,imageTextName, imageTextTag, imageTextPrice, imageTextDate, imageTextBrand, imageTextCategory, imageTextSize, imageTextSeason;
    String imgLink;
    String imgAlias;
    String clothTag;
    String clothPrice;
    String dateInsert;
    String imgId, imgName;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;


    Button editAttr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image_gallery_of_collage);

        imgContainer = (ImageView)findViewById(R.id.imageContainer);
        txtAlias = (TextView)findViewById(R.id.textAlias);
        editAttr = (Button)findViewById(R.id.buttonEditAttr);

        imageTextName = findViewById(R.id.imgName);
        /*imageTextCategory = findViewById(R.id.imgCategory);
        imageTextBrand = findViewById(R.id.imgBrand);
        imageTextPrice = findViewById(R.id.imgPrice);
        imageTextSeason = findViewById(R.id.imgSeason);
        imageTextTag = findViewById(R.id.imgTag);
        imageTextSize = findViewById(R.id.imgSize); imageTextSleeve = findViewById(R.id.imgSleeves);
*/
        imageTextDate = findViewById(R.id.imgDate);

        Intent intent = getIntent();

        imgId = intent.getStringExtra("imgId");
        dateInsert = intent.getStringExtra("date");
        imgLink = intent.getStringExtra("link");
        /*imgLink = intent.getStringExtra("link");
        imgAlias = intent.getStringExtra("alias");
        clothTag = intent.getStringExtra("tag");
        clothPrice = intent.getStringExtra("price");
        dateInsert = intent.getStringExtra("date");*/


        Glide.with(this)
                .load(imgLink)
                .into(imgContainer);
        //txtAlias.setText(imgId+" "+imgAlias +", "+clothTag+ ", " +clothPrice+ ", " +dateInsert);


        editAttr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(ShowImageGalleryOfCollage.this, UpdateCollageDetail.class);
                i.putExtra("imageId", imgId);
                i.putExtra("imageName", imgName);
                i.putExtra("imageUrl", imgLink);
                startActivity(i);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        //DatabaseReference myRef = firebaseDatabase.getReference("users");
        DatabaseReference databaseReference = firebaseDatabase.getReference("users").child(firebaseAuth.getUid()).child("userCollage").child(imgId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ImageUploadInfo userProfile = dataSnapshot.getValue(ImageUploadInfo.class);
                imageTextName.setText(userProfile.getImageName());
                imageTextDate.setText(userProfile.getDate());
                /*imageTextCategory.setText(userProfile.getcategory());
                imageTextBrand.setText(userProfile.getbrand());
                imageTextPrice.setText(userProfile.getprice());
                imageTextSeason.setText(userProfile.getseason());
                imageTextTag.setText(userProfile.gettag());
                imageTextSize.setText(userProfile.getsize());
                imageTextDate.setText(userProfile.getdate());
                imageTextSleeve.setText(userProfile.getsleeve());*/
                imgName = userProfile.getImageName();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ShowImageGalleryOfCollage.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });







    }   @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
