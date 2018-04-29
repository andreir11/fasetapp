package com.example.andre.fasetapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SecondActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private TextView textViewUserId;
    private Button Clothecabinet;
    private Button customize;
    private Button gallery;
    private Button add;
    private String userIdString;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private ImageView imgchoose;


    private Button logout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        firebaseAuth = FirebaseAuth.getInstance();

        textViewUserId = (TextView)findViewById(R.id.textView);
        logout = (Button)findViewById(R.id.btnLogout);
        Clothecabinet= (Button)findViewById(R.id.clothecabinet);


        customize = (Button)findViewById(R.id.buttonAddCustomize);
        gallery = (Button)findViewById(R.id.btnGallery);
        imgchoose = (ImageView)findViewById(R.id.imageView3);
        add=(Button)findViewById(R.id.button7);
        FirebaseUser userLogin = FirebaseAuth.getInstance().getCurrentUser();

        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("users");

        userIdString = mFirebaseDatabase.push().getKey();
        //userIdString = userLogin.toString();

        mFirebaseDatabase.push().getKey();
        User user = new User();

        /*Glide.with(getApplicationContext())
                .load("https://firebasestorage.googleapis.com/v0/b/fasetapp-e5b56.appspot.com/o/image%2Fwhite_1.jpg?alt=media&token=c6cd5a49-7e5f-4352-9f02-5a7a5fcb4ccc")
                .override(300, 200)
                .into(imgchoose);*/

        UserProfile userProfile = new UserProfile();
        String name = userProfile.getUserName();
        //user.setUserId(userIdString);
        //String idOfUser=user.getUserId();
        textViewUserId.setText(firebaseAuth.getCurrentUser().getUid());

        customize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SecondActivity.this, PickFashion.class));
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SecondActivity.this,  DisplayImagesGalleryActivity.class));
            }
        });

        Clothecabinet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             Intent it=new Intent(SecondActivity.this,VirturalCabinet.class);
             it.putExtra("ID",firebaseAuth.getCurrentUser().getUid());
             startActivity(it);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SecondActivity.this,  GalleryActivity.class));
            }
        });



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logout();
            }
        });
    }

    private void Logout(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(SecondActivity.this, MainActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.logoutMenu:{
                Logout();
            }
            case R.id.profileMenu:
                startActivity(new Intent(SecondActivity.this, ProfileActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}